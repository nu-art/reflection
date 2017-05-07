/*
 * The reflection project, is collection of reflection tools I've picked up
 * along the way, use it wisely!
 *
 * Copyright (C) 2017  Adam van der Kruk aka TacB0sS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nu.art.software.reflection.tools;


import com.nu.art.software.core.exceptions.runtime.BadImplementationException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Vector;

@SuppressWarnings("unused")
public class ART_Tools {

	public static <Type> Field[] getFieldsWithAnnotationAndTypeFromClassHierarchy(Class<Type> child, Class<? super Type> topParent, Class<? extends Annotation> classAnnotationType, Class<? extends Annotation> fieldAnnotationType, Class<?>... fieldTypes) {
		Class<?>[] hierarchy = getClassHierarchy(child, topParent, classAnnotationType);
		return getAllFieldsWithAnnotationAndType(hierarchy, fieldTypes, fieldAnnotationType);
	}

	public static <Type> Class<?>[] getClassHierarchy(Class<Type> child, Class<? super Type> topParent, Class<? extends Annotation> annotationType) {
		if (!topParent.isAssignableFrom(child)) {
			throw new BadImplementationException("The class type: '" + topParent.getName() + "' is not a superclass of: '" + child.getName() + "'");
		}
		Vector<Class<?>> typeHierarchy = new Vector<>();
		Class<?> _class = child;

		while (true) {
			if (annotationType != null) {
				Annotation tempTypeAnnotation = _class.getAnnotation(annotationType);
				if (tempTypeAnnotation != null)
					typeHierarchy.insertElementAt(_class, 0);
			} else
				typeHierarchy.insertElementAt(_class, 0);

			_class = _class.getSuperclass();
			if (_class == topParent)
				break;
		}

		return typeHierarchy.toArray(new Class<?>[typeHierarchy.size()]);
	}

	public static Field[] getAllFieldsFromClasses(Class<?>[] classes) {
		Vector<Field> fieldList = new Vector<>();
		for (Class<?> _class : classes) {
			Field[] fields = _class.getDeclaredFields();
			Collections.addAll(fieldList, fields);
		}

		return fieldList.toArray(new Field[fieldList.size()]);
	}

	public static Field[] getAllFieldsWithAnnotationAndType(Class<?>[] classes, Class<?>[] fieldTypes, Class<? extends Annotation> annotationType) {
		Vector<Field> fieldList = new Vector<>();
		for (Class<?> _class : classes) {
			Field[] fields = getAllFieldsWithAnnotationAndType(_class, fieldTypes, annotationType);
			Collections.addAll(fieldList, fields);
		}
		return fieldList.toArray(new Field[fieldList.size()]);
	}

	public static Field[] getAllFieldsWithAnnotationAndType(Class<?> _class, Class<?>[] fieldTypes, Class<? extends Annotation> annotationType) {
		Field[] fields;
		fields = _class.getDeclaredFields();
		Vector<Field> fieldList = new Vector<>();
		boolean match;
		for (Field field : fields) {
			match = false;
			for (Class<?> fieldType : fieldTypes) {
				if (fieldType.isAssignableFrom(field.getType())) {
					match = true;
					break;
				}
			}
			if (!match && fieldTypes.length > 0)
				continue;

			if (annotationType == null || field.getAnnotation(annotationType) != null)
				fieldList.add(field);
		}
		return fieldList.toArray(new Field[fieldList.size()]);
	}

	@SafeVarargs
	public static Field[] getAllFieldsWithAnnotation(Class<?> _class, Class<? extends Annotation>... annotationTypes) {
		Field[] fields = _class.getDeclaredFields();
		return getAllFieldsWithAnnotation(fields, annotationTypes);
	}

	public static Field[] getAllFieldsWithAnnotation(Field[] fields, Class<? extends Annotation>[] annotationTypes) {
		Vector<Field> fieldList = new Vector<>();
		for (Field field : fields) {
			for (Class<? extends Annotation> annotationType : annotationTypes) {
				if (field.getAnnotation(annotationType) == null)
					continue;

				fieldList.add(field);
				break;
			}
		}
		return fieldList.toArray(new Field[fieldList.size()]);
	}

	public static Field getFirstFieldsWithAnnotation(Field[] fields, Class<? extends Annotation> annotationType) {
		for (Field field : fields) {
			if (field.getAnnotation(annotationType) != null)
				return field;
		}
		return null;
	}

	public static Method[] getAllMethodsWithAnnotation(Class<?> _class, Class<? extends Annotation> annotationType) {
		Method[] methods = _class.getDeclaredMethods();
		Vector<Method> methodList = new Vector<>();
		for (Method method : methods) {
			if (method.getAnnotation(annotationType) != null)
				methodList.add(method);
		}
		return methodList.toArray(new Method[methodList.size()]);
	}
}
