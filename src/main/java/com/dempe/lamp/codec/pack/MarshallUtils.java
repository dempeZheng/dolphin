package com.dempe.lamp.codec.pack;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Dempe
 * Date: 2016/1/28
 * Time: 15:25
 * To change this template use File | Settings | File Templates.
 */
public class MarshallUtils {
    private static Set<Type> supported = new HashSet<Type>();

    static {
        supported.add(int.class);
        supported.add(short.class);
        supported.add(long.class);
        supported.add(byte.class);
        supported.add(boolean.class);
        supported.add(String.class);
        supported.add(Integer.class);
        supported.add(Short.class);
        supported.add(Long.class);
        supported.add(Byte.class);
        supported.add(Boolean.class);
        supported.add(List.class);
        supported.add(Map.class);
    }

    public static Set<Type> getSupportedTypes() {
        return supported;
    }


    public static void validateClass(Class<?> clazz) {
        if (!getSupportedTypes().contains(clazz) && !Marshallable.class.isAssignableFrom(clazz)) {
            throw new PackException("pack type not supported : " + clazz);
        }
    }

    public static <T> void packSimpleType(Pack pack, T object, Class<T> clazz) {
        if (object == null) {
            return;
        }
        validateClass(clazz);
        if (clazz.isPrimitive()) {
            if (short.class.isAssignableFrom(clazz)) {
                pack.putShort((Short) object);
            } else if (int.class.isAssignableFrom(clazz)) {
                pack.putInt((Integer) object);
            } else if (long.class.isAssignableFrom(clazz)) {
                pack.putLong((Long) object);
            } else if (boolean.class.isAssignableFrom(clazz)) {
                pack.putBoolean((Boolean) object);
            } else if (byte.class.isAssignableFrom(clazz)) {
                pack.putByte((Byte) object);
            }
        } else {
            if (String.class.isAssignableFrom(clazz)) {
                pack.putVarstr((String) object);
            } else if (Short.class.isAssignableFrom(clazz)) {
                pack.putShort((Short) object);
            } else if (Integer.class.isAssignableFrom(clazz)) {
                pack.putInt((Integer) object);
            } else if (Long.class.isAssignableFrom(clazz)) {
                pack.putLong((Long) object);
            } else if (Boolean.class.isAssignableFrom(clazz)) {
                pack.putBoolean((Boolean) object);
            } else if (Byte.class.isAssignableFrom(clazz)) {
                pack.putByte((Byte) object);
            }
            if (Marshallable.class.isAssignableFrom(clazz)) {
                try {
                    pack.putMarshallable((Marshallable) object);
                } catch (Exception e) {
                    throw new PackException(e);
                }
            }
        }
    }

    public static <T> void packList(Pack pack, List<T> list, Class<T> clazz) {
        validateClass(clazz);
        if (list == null || list.isEmpty()) {
            //list的size
            pack.putInt(0);
            return;
        }
        pack.putInt(list.size());
        for (int i = 0; i < list.size(); i++) {
            packSimpleType(pack, list.get(i), clazz);
        }
    }


    public static <K, V> void packMap(Pack pack, Map<K, V> map, Class<K> primitiveClazzK, Class<V> primitiveClazzV) {
        validateClass(primitiveClazzK);
        validateClass(primitiveClazzV);
        if (map == null || map.size() == 0) {
            //map的size
            pack.putInt(0);
            return;
        }
        pack.putInt(map.size());
        Set<K> keys = map.keySet();
        for (K key : keys) {
            packSimpleType(pack, key, primitiveClazzK);
            packSimpleType(pack, map.get(key), primitiveClazzV);
        }
    }

    public static void packMarshallable(Pack pack, Marshallable mar) {
        if (mar != null) {
            try {
                pack.putMarshallable(mar);
            } catch (Exception e) {
                throw new PackException(e);
            }
        }
    }


    public static <T> T unpackSimpleType(Unpack unpack, Class<T> clazz) {
        validateClass(clazz);
        Object object = null;
        if (clazz.isPrimitive()) {
            if (short.class.isAssignableFrom(clazz)) {
                object = unpack.popShort();
            } else if (int.class.isAssignableFrom(clazz)) {
                object = unpack.popInt();
            } else if (long.class.isAssignableFrom(clazz)) {
                object = unpack.popLong();
            } else if (byte.class.isAssignableFrom(clazz)) {
                object = unpack.popByte();
            } else if (boolean.class.isAssignableFrom(clazz)) {
                object = unpack.popBoolean();
            }
        } else {
            if (String.class.isAssignableFrom(clazz)) {
                object = clazz.cast(unpack.popVarstr());
            }
            if (Short.class.isAssignableFrom(clazz)) {
                object = clazz.cast(unpack.popShort());
            } else if (Integer.class.isAssignableFrom(clazz)) {
                object = clazz.cast(unpack.popInt());
            } else if (Long.class.isAssignableFrom(clazz)) {
                object = clazz.cast(unpack.popLong());
            } else if (Byte.class.isAssignableFrom(clazz)) {
                object = clazz.cast(unpack.popByte());
            } else if (Boolean.class.isAssignableFrom(clazz)) {
                object = clazz.cast(unpack.popBoolean());
            }
            if (Marshallable.class.isAssignableFrom(clazz)) {
                Marshallable mar = null;
                try {
                    mar = (Marshallable) clazz.newInstance();
                    unpack.popMarshallable(mar);
                } catch (InstantiationException e) {
                    throw new UnpackException(e);
                } catch (IllegalAccessException e) {
                    throw new UnpackException(e);
                } catch (Exception e) {
                    throw new UnpackException(e);
                }
                return clazz.cast(mar);
            }
        }
        // object一定和T一致
        @SuppressWarnings("unchecked")
        T ret = (T) object;
        return ret;
    }

    public static <T> List<T> unpackList(Unpack unpack, Class<T> clazz) {
        List<T> list = new ArrayList<T>();
        int size = unpack.popInt();
        if (size == 0) return list;
        for (int i = 0; i < size; i++) {
            list.add(clazz.cast(unpackSimpleType(unpack, clazz)));
        }
        return list;
    }

    public static <K, V> Map<K, V> unpackMap(Unpack unpack, Class<K> primitiveClazzK, Class<V> primitiveClazzV, boolean ordered) {
        Map<K, V> map = null;
        if (ordered) {
            map = new LinkedHashMap<K, V>();
        } else {
            map = new HashMap<K, V>();
        }
        int size = unpack.popInt();
        if (size == 0) return map;
        for (int i = 0; i < size; i++) {
            K key = unpackSimpleType(unpack, primitiveClazzK);
            V value = unpackSimpleType(unpack, primitiveClazzV);
            map.put(key, value);
        }
        return map;
    }

    public static <T extends Marshallable> T unpackMarshall(Unpack unpack, Class<T> marClazz) {
        Marshallable mar;
        try {
            mar = marClazz.newInstance();
            unpack.popMarshallable(mar);
        } catch (InstantiationException e) {
            throw new PackException(e);
        } catch (IllegalAccessException e) {
            throw new PackException(e);
        } catch (Exception e) {
            throw new PackException(e);
        }

        return marClazz.cast(mar);
    }


    @SuppressWarnings("unchecked")
    public static <T> T unpackObject(Unpack unpack, Type genericType) {
        Type[] argTypes = null;
        Class<?> clazz = null;
        if (genericType instanceof ParameterizedType) {
            ParameterizedType aType = (ParameterizedType) genericType;
            argTypes = aType.getActualTypeArguments();
            clazz = (Class<?>) aType.getRawType();
        } else {
            clazz = (Class<?>) genericType;
        }

        Object value = null;
        try {
            if (List.class.isAssignableFrom(clazz)) {
                Class<?> typeClasz = clazz;
                if (argTypes != null && argTypes.length > 0) {
                    typeClasz = (Class<?>) argTypes[0];
                }
                value = unpackList(unpack, typeClasz);
            } else if (Map.class.isAssignableFrom(clazz)) {
                Class<?> primitiveClazzK = (Class<?>) argTypes[0];
                Class<?> primitiveClazzV = (Class<?>) argTypes[1];
                value = unpackMap(unpack, primitiveClazzK, primitiveClazzV, false);
            } else {
                value = MarshallUtils.unpackSimpleType(unpack, clazz);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        T ret = (T) value;
        return ret;
    }

}
