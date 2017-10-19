package com.dima.hptf.ui.action.impl;

import com.dima.hptf.ui.action.ObjectAction;
import com.dima.hptf.ui.element.AbstractComponent;
import org.openqa.selenium.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;


/**
 * Created by dmalinovschi on 12/28/2016.
 */
public class IsDisplayedAction extends AbstractAction implements ObjectAction<Boolean> {
    private static Logger logger = LoggerFactory.getLogger(IsDisplayedAction.class);

    public IsDisplayedAction(AbstractComponent element){
        super(element.getBrowser(), element);
    }

    public Boolean execute() {
        Supplier supplier = () -> {
            try {
                return Boolean.valueOf(this.element.find().isDisplayed());
            } catch (TimeoutException var2) {
                return Boolean.valueOf(false);
            }
        };

        Boolean displayed = (Boolean)super.execute(supplier);
        logger.info("Check element " + this.element.toString() + " is displayed");
        return displayed;
    }
}
