package de.gerrygames.reflection.methods;

import de.gerrygames.reflection.ReflectionFactory;

import java.util.function.Function;

public class FieldModifierMethod<C, T> {
	private GetterMethod<C, T> getter;
	private SetterMethod<C, T> setter;

	private FieldModifierMethod(GetterMethod<C, T> getter, SetterMethod<C, T> setter) {
		this.getter = getter;
		this.setter = setter;
	}

	public static <C, T> FieldModifierMethod<C, T> create(Class<? extends C> owner, String fieldName) {
		GetterMethod<C, T> getter = ReflectionFactory.newGetterMethod(owner, fieldName);
		SetterMethod<C, T> setter = ReflectionFactory.newSetterMethod(owner, fieldName);
		if (setter != null || getter != null) {
			return new FieldModifierMethod<>(getter, setter);
		} else {
			return null;
		}
	}

	public void modify(C instance, Function<T, T> function) {
		setter.set(instance, function.apply(getter.get(instance)));
	}

	public T getAndModify(C instance, Function<T, T> function) {
		T value = getter.get(instance);
		setter.set(instance, function.apply(value));
		return value;
	}

	public T modifyAndGet(C instance, Function<T, T> function) {
		T value = function.apply(getter.get(instance));
		setter.set(instance, value);
		return value;
	}

	public T get(C instance) {
		return getter.get(instance);
	}

	public void set(C instance, T value) {
		setter.set(instance, value);
	}

	public BoundFieldModifierMethod<C, T> bindTo(C instance) {
		return new BoundFieldModifierMethod<>(getter.bindTo(instance), setter.bindTo(instance));
	}
}
