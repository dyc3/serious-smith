package main.java;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/** Used to mark functions used to perform attacks. **/
@Retention(RetentionPolicy.RUNTIME)
public @interface HandlesAttack
{
	/** Which attack the function handles.
	 * @return BossAttack value **/
	BossAttack attack();
}
