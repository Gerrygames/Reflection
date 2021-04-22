package de.gerrygames.reflection.methods;

import de.gerrygames.reflection.ReflectionFactory;
import de.gerrygames.reflection.utils.ReflectionUtils;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class StaticGetterMethod<C, T> extends ReflectedMethod implements IBoundGetterMethod<T> {

	private StaticGetterMethod(MethodHandle handle) {
		super(handle);
	}

	public static <C, T> StaticGetterMethod<C, T> create(Class<? extends C> owner, String fieldName) {
		Field field = ReflectionUtils.findAnyField(owner, fieldName);

		if (field != null && Modifier.isStatic(field.getModifiers())) {
			ReflectionUtils.setAccessible(field);

			try {
				return new StaticGetterMethod<>(ReflectionFactory.lookup().unreflectGetter(field));
			} catch (IllegalAccessException ignored) {}
		}

		return null;
	}

	public T invoke() {
		return this.get();
	}

	public T get() {
		try {
			return (T) this.handle.invoke();
		} catch (Throwable throwable) {
			throw new RuntimeException(throwable);
		}
	}
}
