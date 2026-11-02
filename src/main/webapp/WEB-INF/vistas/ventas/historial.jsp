<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="titulo" value="Historial de ventas"/>
<%@ include file="../_cabecera.jspf" %>
<h1>Historial de ventas</h1>
<p><a class="boton" href="${ctx}/ventas/nueva">Nueva venta</a></p>
<table>
    <tr><th>Folio</th><th>Fecha</th><th>Cliente</th><th>Vendedor</th><th class="num">Total</th><th>Estado</th></tr>
    <c:forEach var="v" items="${ventas}">
        <tr>
            <td><a href="${ctx}/ventas/${v.id}">${v.folio}</a></td>
            <td>${fn:replace(fn:substring(v.fecha, 0, 16), 'T', ' ')}</td>
            <td><c:out value="${v.cliente}"/></td>
            <td><c:out value="${v.vendedor.nombre}"/></td>
            <td class="num">$<fmt:formatNumber value="${v.total}" pattern="#,##0.00"/></td>
            <td><span class="etiqueta ${v.estado}">${v.estado}</span></td>
        </tr>
    </c:forEach>
    <c:if test="${empty ventas}"><tr><td colspan="6">Todavía no hay ventas.</td></tr></c:if>
</table>
<%@ include file="../_pie.jspf" %>
