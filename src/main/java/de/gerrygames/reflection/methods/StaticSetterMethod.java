package de.gerrygames.reflection.methods;

import de.gerrygames.reflection.ReflectionFactory;
import de.gerrygames.reflection.utils.ReflectionUtils;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class StaticSetterMethod<C, T> extends ReflectedMethod implements IBoundSetterMethod<T> {

	private StaticSetterMethod(MethodHandle handle) {
		super(handle);
	}

	public static <C, T> StaticSetterMethod<C, T> create(Class<? extends C> owner, String fieldName) {
		Field field = ReflectionUtils.findAnyField(owner, fieldName);

		if (field != null && Modifier.isStatic(field.getModifiers())) {
			ReflectionUtils.setAccessible(field);
			ReflectionUtils.setFieldNotFinal(field);

			try {
				return new StaticSetterMethod<>(ReflectionFactory.lookup().unreflectSetter(field));
			} catch (IllegalAccessException ignored) {}
		}

		return null;
	}


	public void invoke(T value) {
		this.set(value);
	}

	public void set(T value) {
		try {
			this.handle.invoke(value);
		} catch (Throwable throwable) {
			throw new RuntimeException(throwable);
		}
	}
}
