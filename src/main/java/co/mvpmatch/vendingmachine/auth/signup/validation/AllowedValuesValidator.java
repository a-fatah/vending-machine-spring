package co.mvpmatch.vendingmachine.auth.signup.validation;

import co.mvpmatch.vendingmachine.auth.signup.validation.AllowedValues;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;

public class AllowedValuesValidator implements ConstraintValidator<AllowedValues, String> {

    private String[] allowedValues;

    @Override
    public void initialize(AllowedValues constraintAnnotation) {
        this.allowedValues = constraintAnnotation.values();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null) { // Optional check if you wish to allow nulls
            return true;
        }
        return Arrays.asList(allowedValues).contains(value);
    }

}
