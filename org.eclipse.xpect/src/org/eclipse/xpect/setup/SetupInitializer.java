package org.eclipse.xpect.setup;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.eclipse.xpect.Assignment;
import org.eclipse.xpect.BooleanLiteral;
import org.eclipse.xpect.ClassLiteral;
import org.eclipse.xpect.Component;
import org.eclipse.xpect.Instance;
import org.eclipse.xpect.IntLiteral;
import org.eclipse.xpect.StringLiteral;
import org.eclipse.xpect.Value;
import org.eclipse.xtext.common.types.JvmOperation;
import org.eclipse.xtext.common.types.util.JavaReflectAccess;

@SuppressWarnings("restriction")
public class SetupInitializer {

	public void initialize(Object obj, Instance init) {
		JavaReflectAccess jra = new JavaReflectAccess();
		for (Assignment a : init.getAssignments()) {
			JvmOperation target = a.getTarget();
			if (target != null) {
				Method m = jra.getMethod(target);
				try {
					m.invoke(obj, create(a.getValue()));
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.getCause().printStackTrace();
				}
			}
		}
	}

	protected Object create(Component val) {
		Class<?> type = new JavaReflectAccess().getRawType(val.getType());
		try {
			Object result = type.newInstance();
			initialize(result, val);
			return result;
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	protected Object create(BooleanLiteral val) {
		return val.isValue();
	}

	protected Object create(StringLiteral val) {
		return val.getValue();
	}

	protected Object create(IntLiteral val) {
		return val.getValue();
	}

	protected Object create(ClassLiteral val) {
		return new JavaReflectAccess().getRawType(val.getType());
	}

	protected Object create(Value val) {
		if (val instanceof Component)
			return create((Component) val);
		if (val instanceof BooleanLiteral)
			return create((BooleanLiteral) val);
		if (val instanceof StringLiteral)
			return create((StringLiteral) val);
		if (val instanceof IntLiteral)
			return create((IntLiteral) val);
		if (val instanceof ClassLiteral)
			return create((ClassLiteral) val);
		return null;
	}
}
