package com.ezar.clickandeat.web.tags;

import com.ezar.clickandeat.config.MessageFactory;
import org.springframework.util.StringUtils;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;
import java.util.SortedSet;
import java.util.TreeSet;

public class CuisineTag extends SimpleTagSupport {

    private final SortedSet<String> cuisineList;

    private final SortedSet<String> footerCuisineList;

    private final SortedSet<String> locationList;


    public CuisineTag() {
        this.cuisineList = new TreeSet<String>(StringUtils.commaDelimitedListToSet(MessageFactory.getMessage("restaurants.cuisines", false)));
        this.footerCuisineList = new TreeSet<String>(StringUtils.commaDelimitedListToSet(MessageFactory.getMessage("restaurants.footercuisines", false)));
        this.locationList = new TreeSet<String>(StringUtils.commaDelimitedListToSet(MessageFactory.getMessage("restaurants.locations", false)));
    }

    @Override
    public void doTag() throws JspException, IOException {
        getJspContext().setAttribute("cuisines", cuisineList);
        getJspContext().setAttribute("footerCuisines", footerCuisineList);
        getJspContext().setAttribute("locations", locationList);
    }
}
