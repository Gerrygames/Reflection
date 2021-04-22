package de.gerrygames.reflection.methods;

import java.lang.invoke.MethodHandle;

public class BoundSetterMethod<C, T> extends ReflectedMethod implements IBoundSetterMethod<T> {

	BoundSetterMethod(MethodHandle handle) {
		super(handle);
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
