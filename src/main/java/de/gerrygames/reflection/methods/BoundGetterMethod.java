package de.gerrygames.reflection.methods;

import java.lang.invoke.MethodHandle;

public class BoundGetterMethod<C, T> extends ReflectedMethod implements IBoundGetterMethod<T> {

	BoundGetterMethod(MethodHandle handle) {
		super(handle);
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
