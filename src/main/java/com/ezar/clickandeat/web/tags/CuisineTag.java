package com.ezar.clickandeat.web.tags;

import com.ezar.clickandeat.util.CuisineProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.tags.RequestContextAwareTag;

@Configurable
public class CuisineTag extends RequestContextAwareTag {

    @Autowired
    private CuisineProvider cuisineProvider;

    @Override
    protected int doStartTagInternal() throws Exception {
        if( cuisineProvider == null ) {
            WebApplicationContext ctx = getRequestContext().getWebApplicationContext();
            AutowireCapableBeanFactory factory = ctx.getAutowireCapableBeanFactory();
            factory.autowireBean(this);
        }
        
        pageContext.setAttribute("cuisines", cuisineProvider.getCuisineList());
        pageContext.setAttribute("cuisineLocations", cuisineProvider.getCuisineLocations());
        return SKIP_BODY;
    }

}
