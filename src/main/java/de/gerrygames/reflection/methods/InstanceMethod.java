package de.gerrygames.reflection.methods;

import de.gerrygames.reflection.ReflectionFactory;
import de.gerrygames.reflection.utils.ReflectionUtils;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class InstanceMethod<C, R> extends ReflectedMethod {

	private InstanceMethod(MethodHandle handle) {
		super(handle);
	}

	public static <C, R> InstanceMethod<C, R> create(Class<? extends C> owner, String name, Class<?>... parameterTypes) {
		Method method = ReflectionUtils.findAnyMethod(owner, name, parameterTypes);

		if (method != null && !Modifier.isStatic(method.getModifiers())) {
			ReflectionUtils.setAccessible(method);

			try {
				return new InstanceMethod<>(ReflectionFactory.lookup().unreflect(method));
			} catch (IllegalAccessException ignored) {}
		}

		return null;
	}

	public R invoke(C instance, Object... parameters) {
		try {
			return (R) this.handle.bindTo(instance).invokeWithArguments(parameters);
		} catch (Throwable throwable) {
			throw new RuntimeException(throwable);
		}
	}

	public BoundInstanceMethod<C, R> bindTo(C instance) {
		return new BoundInstanceMethod<>(this.handle.bindTo(instance));
	}
}
