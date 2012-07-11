<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>

<div id="workflow">
    <table class="tabstable" cellpadding="0" cellspacing="0" border="0" width="100%">
        <thead>
            <tr>
                <th width="25%"><div class="workflowtab workflowtableft workflowtabactive">1. <spring:message code="label.find-restaurant"/></div></th>
                <th width="25%"><div class="workflowtab">2. <spring:message code="label.build-order"/></div></th>
                <th width="25%"><div class="workflowtab">3. <spring:message code="label.checkout"/></div></th>
                <th width="25%"><div class="workflowtab workflowtabright">4. <spring:message code="label.payment"/></div></th>
            </tr>
        </thead>
    </table>
</div>