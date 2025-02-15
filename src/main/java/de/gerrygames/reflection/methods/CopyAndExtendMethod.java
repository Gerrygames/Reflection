package de.gerrygames.reflection.methods;

import de.gerrygames.reflection.utils.ReflectionUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class CopyAndExtendMethod<O, E extends O> {
	private final Constructor<E> emptyConstructor;
	private final List<FieldModifierMethod<Object, Object>> fieldModifiers;

	public static <O, E extends O> CopyAndExtendMethod<O, E> create(Class<O> clazz, Class<E> superClazz) {
		Constructor<E> emptyConstructor = ReflectionUtils.getEmptyConstructor(superClazz);
		if (emptyConstructor == null) return null;
		List<FieldModifierMethod<Object, Object>> fieldModifiers = new LinkedList<>();
		Class<?> current = clazz;
		do {
			for (Field field : current.getDeclaredFields()) {
				FieldModifierMethod<Object, Object> modifier = FieldModifierMethod.create(field);
				if (modifier != null) fieldModifiers.add(modifier);
			}
		} while (((current = current.getSuperclass()) != Object.class));
		return new CopyAndExtendMethod<>(emptyConstructor, fieldModifiers);
	}

	public E copyAndExtend(O object) {
		E copy = ReflectionUtils.getEmptyObject(emptyConstructor);
		for (FieldModifierMethod<Object, Object> fieldModifier : fieldModifiers) {
			fieldModifier.set(copy, fieldModifier.get(object));
		}
		return copy;
	}
}
