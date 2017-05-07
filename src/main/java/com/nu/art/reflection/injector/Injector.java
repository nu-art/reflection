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

package com.nu.art.reflection.injector;

import java.lang.reflect.Field;

import com.nu.art.core.exceptions.runtime.BadImplementationException;

/**
 * The injectee type that this interface supports.
 *
 * @param <BaseType>
 *
 * @author TacB0sS
 */
public abstract class Injector<BaseType, InjecteeBaseType> {

	protected final void injectInstanceToField(Object instance, Field field, Object fieldValue) {
		try {
			boolean accessible = field.isAccessible();
			if (!accessible)
				field.setAccessible(true);
			field.set(instance, fieldValue);
			if (!accessible)
				field.setAccessible(false);
		} catch (Exception e) {
			throw new BadImplementationException("Expected value for field: '" + field + "', actual value type: '" + fieldValue.getClass().getName() + "'", e);
		}
	}

	protected abstract Field[] extractFieldsFromInstance(Class<? extends InjecteeBaseType> injecteeType);

	@SuppressWarnings("unchecked")
	public final <InjecteeType extends InjecteeBaseType> void injectToInstance(InjecteeType instance) {
		Field[] validFieldsForInjection = extractFieldsFromInstance((Class<? extends InjecteeBaseType>) instance.getClass());
		for (Field field : validFieldsForInjection) {
			Object fieldValue = getValueForField(field);
			injectInstanceToField(instance, field, fieldValue);
		}
	}

	/**
	 * @param field The field for which to get the value for.
	 *
	 * @return a value for the field
	 */
	protected abstract Object getValueForField(Field field);
}
