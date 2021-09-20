package de.gerrygames.reflection.methods;

import de.gerrygames.reflection.ReflectionFactory;

import java.util.function.Function;

public class BoundFieldModifierMethod<C, T> {
	private IBoundGetterMethod<T> getter;
	private IBoundSetterMethod<T> setter;

	BoundFieldModifierMethod(IBoundGetterMethod<T> getter, IBoundSetterMethod<T> setter) {
		this.getter = getter;
		this.setter = setter;
	}

	public static <C, T> BoundFieldModifierMethod<C, T> create(Class<? extends C> owner, String fieldName, Class<T> fieldType) {
		return new BoundFieldModifierMethod<>(ReflectionFactory.newStaticGetterMethod(owner, fieldType, fieldName), ReflectionFactory.newStaticSetterMethod(owner, fieldType, fieldName));
	}

	public void modify(Function<T, T> function) {
		setter.set(function.apply(getter.get()));
	}

	public T getAndModify(Function<T, T> function) {
		T value = getter.get();
		setter.set(function.apply(value));
		return value;
	}

	public T modifyAndGet(Function<T, T> function) {
		T value = function.apply(getter.get());
		setter.set(value);
		return value;
	}

	public T get() {
		return getter.get();
	}

	public void set(T value) {
		setter.set(value);
	}
}
