package com.ezar.clickandeat.web.tags;

import com.ezar.clickandeat.util.CuisineProvider;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;

public class CuisineTag extends SimpleTagSupport {

    @Override
    public void doTag() throws JspException, IOException {
        getJspContext().setAttribute("cuisines", CuisineProvider.getCuisineList());
        getJspContext().setAttribute("footerCuisines", CuisineProvider.getFooterCuisineMap());
        getJspContext().setAttribute("locations", CuisineProvider.getFooterLocationMap());
    }

}
