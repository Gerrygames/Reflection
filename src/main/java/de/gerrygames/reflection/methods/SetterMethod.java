package de.gerrygames.reflection.methods;

import de.gerrygames.reflection.ReflectionFactory;
import de.gerrygames.reflection.utils.ReflectionUtils;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class SetterMethod<C, T> extends ReflectedMethod {

	private SetterMethod(MethodHandle handle) {
		super(handle);
	}

	public static <C, T> SetterMethod<C, T> create(Class<? extends C> owner, String fieldName, Class<T> fieldType) {
		Field field = ReflectionUtils.findAnyField(owner, fieldName, fieldType);

		if (field != null && !Modifier.isStatic(field.getModifiers())) {
			ReflectionUtils.setAccessible(field);
			ReflectionUtils.setFieldNotFinal(field);

			try {
				return new SetterMethod<>(ReflectionFactory.lookup().unreflectSetter(field));
			} catch (IllegalAccessException ignored) {}
		}

		return null;
	}

	public void invoke(C instance, T value) {
		this.set(instance, value);
	}

	public void set(C instance, T value) {
		try {
			this.handle.invoke(instance, value);
		} catch (Throwable throwable) {
			throw new RuntimeException(throwable);
		}
	}

	public BoundSetterMethod<C, T> bindTo(C instance) {
		return new BoundSetterMethod<>(this.handle.bindTo(instance));
	}
}
