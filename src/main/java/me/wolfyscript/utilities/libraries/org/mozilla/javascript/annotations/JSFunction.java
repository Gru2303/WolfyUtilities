/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package me.wolfyscript.utilities.libraries.org.mozilla.javascript.annotations;

import java.lang.annotation.*;

/**
 * An annotation that marks a Java method as JavaScript function. This can
 * be used as an alternative to the <code>jsFunction_</code> prefix desribed in
 * {@link me.wolfyscript.utilities.libraries.org.mozilla.javascript.ScriptableObject#defineClass(me.wolfyscript.utilities.libraries.org.mozilla.javascript.Scriptable, java.lang.Class)}.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface JSFunction {
    String value() default "";
}