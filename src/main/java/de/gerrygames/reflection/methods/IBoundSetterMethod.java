package de.gerrygames.reflection.methods;

import java.util.function.Consumer;

public interface IBoundSetterMethod<T> extends Consumer<T> {

	default void accept(T value) {
		set(value);
	}

	void set(T value);
}
