package de.gerrygames.reflection.methods;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.lang.invoke.MethodHandle;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class ReflectedMethod {
	protected final MethodHandle handle;
}
