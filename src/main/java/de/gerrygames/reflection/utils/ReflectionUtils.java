package de.gerrygames.reflection.utils;

import de.gerrygames.reflection.ReflectionFactory;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class ReflectionUtils {
	private static MethodHandle setModifiers;
	static {
		if ((setModifiers = findSetModifiersJava8()) != null) {
			System.out.println("[ReflectionUtils] Found Java 8 setter for Field#modifiers.");
		} else if ((setModifiers = findSetModifiersJava9()) != null) {
			System.out.println("[ReflectionUtils] Found Java 9+ setter for Field#modifiers.");
		} else {
			System.out.println("[ReflectionUtils] Unable to find setter for Field#modifiers!");
		}
	}

	private static MethodHandle findSetModifiersJava9() {
		try {
			return ReflectionFactory.lookup().findSetter(Field.class, "modifiers", int.class);
		} catch (NoSuchFieldException | IllegalAccessException ex) {
			return null;
		}
	}

	private static MethodHandle findSetModifiersJava8() {
		Field modifiersField = ReflectionUtils.findAnyField(Field.class, "modifiers");
		if (modifiersField == null) return null;
		setAccessible(modifiersField);
		try {
			return ReflectionFactory.lookup().unreflectSetter(modifiersField);
		} catch (IllegalAccessException ex) {
			return null;
		}
	}

	public static void setFieldNotFinal(Field field) {
		int modifiers = field.getModifiers();
		if (!Modifier.isFinal(modifiers)) return;

		modifiers &= ~Modifier.FINAL;

		try {
			setModifiers.invoke(field, modifiers);
		} catch (Throwable error) {
			throw new RuntimeException(error);
		}
	}

	public static void setAccessible(Field field) {
		if (field.isAccessible()) return;
		field.setAccessible(true);
	}

	public static void setAccessible(Method method) {
		if (method.isAccessible()) return;
		method.setAccessible(true);
	}

	public static void transferValue(Object source, Object target, String field) {
		transferValue(source, target, field, field);
	}

	public static void transferValue(Object source, Object target, String sourceField, String targetField) {
		Field sf = findAnyField(source.getClass(), sourceField);
		Field tf = findAnyField(target.getClass(), targetField);

		setAccessible(sf);
		setAccessible(tf);

		if (Modifier.isFinal(tf.getModifiers())) setFieldNotFinal(tf);

		try {
			tf.set(target, sf.get(source));
		} catch (IllegalAccessException ignored) {}
	}

	public static <E> Constructor<E> getEmptyConstructor(Class<E> clazz) {
		try {
			return getConstructor(clazz, Object.class.getDeclaredConstructor());
		} catch (NoSuchMethodException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static <E> Constructor<E> getConstructor(Class<E> clazz, Constructor<? super E> superConstructor) {
		return (Constructor<E>) sun.reflect.ReflectionFactory.getReflectionFactory().newConstructorForSerialization(clazz, superConstructor);
	}

	public static <E> E getEmptyObject(Class<E> clazz) {
		Constructor<E> constructor = getEmptyConstructor(clazz);
		try {
			return clazz.cast(constructor.newInstance());
		} catch (InstantiationException | IllegalArgumentException | InvocationTargetException | IllegalAccessException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static <O, E extends O> E copyAndExtendObject(O object, Class<E> clazz) {
		if (!object.getClass().isAssignableFrom(clazz)) throw new IllegalArgumentException(clazz.getName() + " is not compatible to " + object.getClass().getName());

		E copy = getEmptyObject(clazz);

		Class<?> current = object.getClass();
		do {
			for (Field f : current.getDeclaredFields()) {
				int modifiers = f.getModifiers();
				if (Modifier.isStatic(modifiers)) continue;
				if (Modifier.isFinal(modifiers)) setFieldNotFinal(f);
				setAccessible(f);
				try {
					f.set(copy, f.get(object));
				} catch (IllegalAccessException ex) {
					ex.printStackTrace();
				}
			}
		} while (((current = current.getSuperclass()) != Object.class));

		return copy;
	}

	public static Method findAnyMethod(Class<?> owner, String name, Class<?>... parameterTypes) {
		Method method;

		if ((method = findMethod(owner, name, parameterTypes)) == null) {
			method = findDeclaredMethod(owner, name, parameterTypes);
		}

		return method;
	}

	public static Method findDeclaredMethod(Class<?> owner, String name, Class<?>... parameterTypes) {
		Method method;

		do {
			try {
				method = owner.getDeclaredMethod(name, parameterTypes);
				return method;
			} catch (NoSuchMethodException ignored) {}
		} while ((owner = owner.getSuperclass()) != null);

		return null;
	}

	public static Method findMethod(Class<?> owner, String name, Class<?>... parameterTypes) {
		try {
			return owner.getMethod(name, parameterTypes);
		} catch (NoSuchMethodException ignored) {
			return null;
		}
	}

	public static Field findAnyField(Class<?> owner, String name) {
		return findAnyField(owner, name, null);
	}

	public static Field findAnyField(Class<?> owner, Class<?> type) {
		return findAnyField(owner, null, type);
	}

	public static Field findAnyField(Class<?> owner, String name, Class<?> type) {
		Field field;

		if ((field = findField(owner, name, type)) != null) {
			;
		} else if ((field = findDeclaredField(owner, name, type)) != null) {
			;
		}

		return field;
	}

	public static Field findDeclaredField(Class<?> owner, String name) {
		return findDeclaredField(owner, name, null);
	}

	public static Field findDeclaredField(Class<?> owner, Class<?> type) {
		return findDeclaredField(owner, null, type);
	}

	public static Field findDeclaredField(Class<?> owner, String name, Class<?> type) {
		if (name != null) {
			Class<?> current = owner;
			do {
				try {
					Field field = current.getDeclaredField(name);
					if (type == null || type.isAssignableFrom(field.getType())) {
						return field;
					}
				} catch (NoSuchFieldException ignored) {}
			} while ((current = current.getSuperclass()) != null);
		}

		if (type != null) {
			Class<?> current = owner;
			do {
				for (Field field : current.getDeclaredFields()) {
					if (type.isAssignableFrom(field.getType()) && (name == null || field.getName().equals(name))) {
						return field;
					}
				}
			} while ((current = current.getSuperclass()) != null);
		}

		return null;
	}

	public static Field findField(Class<?> owner, String name) {
		return findField(owner, name, null);
	}

	public static Field findField(Class<?> owner, Class<?> type) {
		return findField(owner, null, type);
	}

	public static Field findField(Class<?> owner, String name, Class<?> type) {
		if (name != null) {
			try {
				Field field = owner.getField(name);
				if (type == null || matchType(type, field.getType())) {
					return field;
				}
			} catch (NoSuchFieldException ignored) {}
		}

		if (type != null) {
			for (Field field : owner.getFields()) {
				if (matchType(type, field.getType()) && (name == null || field.getName().equals(name))) {
					return field;
				}
			}
		}

		return null;
	}

	private static boolean matchType(Class<?> wanted, Class<?> got) {
		if (wanted.isAssignableFrom(got)) return true;

		if (wanted == Integer.class) {
			return got == Integer.class || got == int.class;
		} else if (wanted == Double.class) {
			return got == Integer.class || got == double.class;
		} else if (wanted == Long.class) {
			return got == Long.class || got == long.class;
		} else if (wanted == Character.class) {
			return got == Character.class || got == char.class;
		} else if (wanted == Byte.class) {
			return got == Byte.class || got == byte.class;
		} else if (wanted == Float.class) {
			return got == Float.class || got == float.class;
		} else if (wanted == Boolean.class) {
			return got == Boolean.class || got == boolean.class;
		}

		return false;
	}
}
