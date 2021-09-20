package de.gerrygames.reflection;

import de.gerrygames.reflection.methods.BoundFieldModifierMethod;
import de.gerrygames.reflection.methods.FieldModifierMethod;
import de.gerrygames.reflection.methods.GetterMethod;
import de.gerrygames.reflection.methods.InstanceMethod;
import de.gerrygames.reflection.methods.SetterMethod;
import de.gerrygames.reflection.methods.StaticGetterMethod;
import de.gerrygames.reflection.methods.StaticMethod;
import de.gerrygames.reflection.methods.StaticSetterMethod;
import sun.misc.Unsafe;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;

public class ReflectionFactory {
	private static MethodHandles.Lookup lookup;
	static {
		if ((lookup = getTrustedLookup()) != null) {
			System.out.println("[ReflectionFactory] Got trusted lookup!");
		} else {
			lookup = MethodHandles.lookup();
			System.out.println("[ReflectionFactory] Using untrusted lookup.");
		}
	}

	private static MethodHandles.Lookup getTrustedLookup() {
		try {
			Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
			theUnsafe.setAccessible(true);
			Unsafe unsafe = (Unsafe) theUnsafe.get(null);
			Field trustedLookup = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
			return (MethodHandles.Lookup) unsafe.getObject(unsafe.staticFieldBase(trustedLookup), unsafe.staticFieldOffset(trustedLookup));
		} catch (NoSuchFieldException | IllegalAccessException ex) {
			return null;
		}
	}

	public static MethodHandles.Lookup lookup() {
		return lookup;
	}

	public static <C, R> StaticMethod<C, R> newStaticMethod(Class<? extends C> owner, Class<R> returnType, String name, Class<?>... parameterTypes) {
		return newStaticMethod(owner, name, parameterTypes);
	}

	public static <C, R> StaticMethod<C, R> newStaticMethod(Class<? extends C> owner, String name, Class<?>... parameterTypes) {
		return StaticMethod.create(owner, name, parameterTypes);
	}

	public static <C, R> InstanceMethod<C, R> newInstanceMethod(Class<? extends C> owner, Class<R> returnType, String name, Class<?>... parameterTypes) {
		return newInstanceMethod(owner, name, parameterTypes);
	}

	public static <C, R> InstanceMethod<C, R> newInstanceMethod(Class<? extends C> owner, String name, Class<?>... parameterTypes) {
		return InstanceMethod.create(owner, name, parameterTypes);
	}

	public static <C, T> GetterMethod<C, T> newGetterMethod(Class<? extends C> owner, String name) {
		return newGetterMethod(owner, null, name);
	}

	public static <C, T> GetterMethod<C, T> newGetterMethod(Class<? extends C> owner, Class<T> type) {
		return newGetterMethod(owner, type, null);
	}

	public static <C, T> GetterMethod<C, T> newGetterMethod(Class<? extends C> owner, Class<T> type, String name) {
		return GetterMethod.create(owner, name, type);
	}

	public static <C, T> SetterMethod<C, T> newSetterMethod(Class<? extends C> owner, String name) {
		return newSetterMethod(owner, null, name);
	}

	public static <C, T> SetterMethod<C, T> newSetterMethod(Class<? extends C> owner, Class<T> type) {
		return newSetterMethod(owner, type, null);
	}

	public static <C, T> SetterMethod<C, T> newSetterMethod(Class<? extends C> owner, Class<T> type, String name) {
		return SetterMethod.create(owner, name, type);
	}

	public static <C, T> FieldModifierMethod<C, T> newFieldModifier(Class<? extends C> owner, String name) {
		return newFieldModifier(owner, null, name);
	}

	public static <C, T> FieldModifierMethod<C, T> newFieldModifier(Class<? extends C> owner, Class<T> type) {
		return newFieldModifier(owner, type, null);
	}

	public static <C, T> FieldModifierMethod<C, T> newFieldModifier(Class<? extends C> owner, Class<T> type, String name) {
		return FieldModifierMethod.create(owner, name, type);
	}

	public static <C, T> BoundFieldModifierMethod<C, T> newStaticFieldModifier(Class<? extends C> owner, String name) {
		return newStaticFieldModifier(owner, null, name);
	}

	public static <C, T> BoundFieldModifierMethod<C, T> newStaticFieldModifier(Class<? extends C> owner, Class<T> type) {
		return newStaticFieldModifier(owner, type, null);
	}

	public static <C, T> BoundFieldModifierMethod<C, T> newStaticFieldModifier(Class<? extends C> owner, Class<T> type, String name) {
		return BoundFieldModifierMethod.create(owner, name, type);
	}

	public static <C, T> StaticGetterMethod<C, T> newStaticGetterMethod(Class<? extends C> owner, String name) {
		return newStaticGetterMethod(owner, null, name);
	}

	public static <C, T> StaticGetterMethod<C, T> newStaticGetterMethod(Class<? extends C> owner, Class<T> type) {
		return newStaticGetterMethod(owner, type, null);
	}

	public static <C, T> StaticGetterMethod<C, T> newStaticGetterMethod(Class<? extends C> owner, Class<T> type, String name) {
		return StaticGetterMethod.create(owner, name, type);
	}

	public static <C, T> StaticSetterMethod<C, T> newStaticSetterMethod(Class<? extends C> owner, String name) {
		return newStaticSetterMethod(owner, null, name);
	}

	public static <C, T> StaticSetterMethod<C, T> newStaticSetterMethod(Class<? extends C> owner, Class<T> type) {
		return newStaticSetterMethod(owner, type, null);
	}

	public static <C, T> StaticSetterMethod<C, T> newStaticSetterMethod(Class<? extends C> owner, Class<T> type, String name) {
		return StaticSetterMethod.create(owner, name, type);
	}
}
