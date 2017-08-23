package com.zhizus.forest.dolphin.utils;

import com.google.common.collect.Sets;

import java.lang.reflect.Method;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ThreadLocalRandom;

public final class ThriftClientUtils {

    private static ConcurrentMap<Class<?>, Set<String>> interfaceMethodCache = new ConcurrentHashMap<>();

    private ThriftClientUtils() {
        throw new UnsupportedOperationException();
    }

    public static final int randomNextInt() {
        return ThreadLocalRandom.current().nextInt();
    }

    public static final int chooseWithChance(int[] args) {
        int argCount = args.length;
        int sumOfChances = 0;

        for (int i = 0; i < argCount; i++) {
            sumOfChances += args[i];
        }

        int random = ThreadLocalRandom.current().nextInt(sumOfChances);

        while (random + args[argCount - 1] < sumOfChances) {
            sumOfChances -= args[--argCount];
        }
        return argCount - 1;
    }

    public static final Set<String> getInterfaceMethodNames(Class<?> ifaceClass) {

        if (interfaceMethodCache.containsKey(ifaceClass))
            return interfaceMethodCache.get(ifaceClass);
        Set<String> methodName = Sets.newHashSet();
        Class<?>[] interfaces = ifaceClass.getInterfaces();
        for (Class<?> class1 : interfaces) {
            Method[] methods = class1.getMethods();
            for (Method method : methods) {
                methodName.add(method.getName());
            }
        }
        interfaceMethodCache.putIfAbsent(ifaceClass, methodName);
        return methodName;
    }
}
