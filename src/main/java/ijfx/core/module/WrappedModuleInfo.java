/*
    This file is part of ImageJ FX.

    ImageJ FX is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    ImageJ FX is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with ImageJ FX.  If not, see <http://www.gnu.org/licenses/>. 
    
     Copyright 2015,2016 Cyril MONGIS, Michael Knop
	
 */
package ijfx.core.module;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.scijava.ItemIO;
import org.scijava.ItemVisibility;
import org.scijava.ValidityProblem;
import org.scijava.command.CommandModuleItem;
import org.scijava.module.AbstractModuleInfo;
import org.scijava.module.Module;
import org.scijava.module.ModuleException;
import org.scijava.module.ModuleItem;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.SciJavaPlugin;
import org.scijava.util.ClassUtils;

/**
 * @author Curtis Rueden
 * @author Cyril MONGIS (imported code from @CommandInfo)
 */
public class WrappedModuleInfo<T extends SciJavaPlugin> extends AbstractModuleInfo {

    final ModuleWrapper<T> wrapper;

   
    HashMap<String,Object> presets = new HashMap<>();
    
    
    List<ValidityProblem> problems = new ArrayList<>();
    
    public WrappedModuleInfo(ModuleWrapper<T> wrapper) {
        this.wrapper = wrapper;
        checkFields(wrapper.getPlugin().getClass());
    }

  
    
    @Override
    public String getDelegateClassName() {
        return wrapper.getPlugin().getClass().getName();
    }

    @Override
    public Class<?> loadDelegateClass() throws ClassNotFoundException {
        return wrapper.getPlugin().getClass();
    }

    @Override
    public Module createModule() throws ModuleException {
        try {
            return new ModuleWrapper((SciJavaPlugin) loadDelegateClass().newInstance());
        } catch (ClassNotFoundException ex) {
            throw new ModuleException(ex);
        } catch (InstantiationException ex) {
            throw new ModuleException(ex);
        } catch (IllegalAccessException ex) {
            throw new ModuleException(ex);
        }

    }

    
    
    /** Processes the given class's @{@link Parameter}-annotated fields. */
	private void checkFields(final Class<?> type) {
		if (type == null) return;

		// NB: Reject abstract classes.
		if (Modifier.isAbstract(type.getModifiers())) {
			problems.add(new ValidityProblem("Delegate class is abstract"));
		}

		final List<Field> fields =
			ClassUtils.getAnnotatedFields(type, Parameter.class);

		for (final Field f : fields) {
			f.setAccessible(true); // expose private fields

			final Parameter param = f.getAnnotation(Parameter.class);

			boolean valid = true;

			final boolean isFinal = Modifier.isFinal(f.getModifiers());
			final boolean isMessage = param.visibility() == ItemVisibility.MESSAGE;
			if (isFinal && !isMessage) {
				// NB: Final parameters are bad because they cannot be modified.
				final String error = "Invalid final parameter: " + f;
				problems.add(new ValidityProblem(error));
				valid = false;
			}

			final String name = f.getName();
			if (inputMap().containsKey(name) || outputMap().containsKey(name)) {
				// NB: Shadowed parameters are bad because they are ambiguous.
				final String error = "Invalid duplicate parameter: " + f;
				problems.add(new ValidityProblem(error));
				valid = false;
			}

			if (param.type() == ItemIO.BOTH && isImmutable(f.getType())) {
				// NB: The BOTH type signifies that the parameter will be changed
				// in-place somehow. But immutable parameters cannot be changed in
				// such a manner, so it makes no sense to label them as BOTH.
				final String error = "Immutable BOTH parameter: " + f;
				problems.add(new ValidityProblem(error));
				valid = false;
			}

			if (!valid) {
				// NB: Skip invalid parameters.
				continue;
			}

			final boolean isPreset = presets.containsKey(name);

			// add item to the relevant list (inputs or outputs)
			final ModuleItem<Object> item =
				new CommandModuleItem(this, f);
			if (item.isInput()) {
				inputMap().put(name, item);
				if (!isPreset) inputList().add(item);
			}
			if (item.isOutput()) {
				outputMap().put(name, item);
				if (!isPreset) outputList().add(item);
			}
		}
	}

	private boolean isImmutable(final Class<?> type) {
		// NB: All eight primitive types, as well as the boxed primitive
		// wrapper classes, as well as strings, are immutable objects.
		return ClassUtils.isNumber(type) || ClassUtils.isText(type) ||
			ClassUtils.isBoolean(type);
	}
}
