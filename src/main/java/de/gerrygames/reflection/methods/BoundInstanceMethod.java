package de.gerrygames.reflection.methods;

import java.lang.invoke.MethodHandle;

public class BoundInstanceMethod<C, R> extends ReflectedMethod {

	BoundInstanceMethod(MethodHandle handle) {
		super(handle);
	}

	public R invoke(Object... parameters) {
		try {
			return (R) this.handle.invokeWithArguments(parameters);
		} catch (Throwable throwable) {
			throw new RuntimeException(throwable);
		}
	}
}
