package de.gerrygames.reflection.methods;

import lombok.Getter;

import java.lang.invoke.MethodHandle;

public abstract class ReflectedMethod {
	@Getter
	protected final MethodHandle handle;

	protected ReflectedMethod(MethodHandle handle) {
		this.handle = handle;
	}
}
