package de.gerrygames.reflection.methods;

import de.gerrygames.reflection.ReflectionFactory;
import de.gerrygames.reflection.utils.ReflectionUtils;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class GetterMethod<C, T> extends ReflectedMethod {

	private GetterMethod(MethodHandle handle) {
		super(handle);
	}

	public static <C, T> GetterMethod<C, T> create(Class<? extends C> owner, String fieldName) {
		Field field = ReflectionUtils.findAnyField(owner, fieldName);

		if (field != null && !Modifier.isStatic(field.getModifiers())) {
			ReflectionUtils.setAccessible(field);

			try {
				return new GetterMethod<>(ReflectionFactory.lookup().unreflectGetter(field));
			} catch (IllegalAccessException ignored) {}
		}

		return null;
	}

	public T invoke(C instance) {
		return this.get(instance);
	}

	public T get(C instance) {
		try {
			return (T) this.handle.invoke(instance);
		} catch (Throwable throwable) {
			throw new RuntimeException(throwable);
		}
	}

	public BoundGetterMethod<C, T> bindTo(C instance) {
		return new BoundGetterMethod<>(this.handle.bindTo(instance));
	}
}
