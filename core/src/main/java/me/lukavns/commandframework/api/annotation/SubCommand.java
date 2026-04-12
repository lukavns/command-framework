package me.lukavns.commandframework.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import me.lukavns.commandframework.api.command.CommandTarget;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SubCommand {

    String value() default "";

    String name() default "";

    String[] aliases() default {};

    String description() default "";

    String usage() default "";

    String permission() default "";

    boolean async() default false;

    CommandTarget target() default CommandTarget.ALL;
}
