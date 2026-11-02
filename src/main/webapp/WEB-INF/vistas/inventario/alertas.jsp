<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="titulo" value="Alertas de stock"/>
<%@ include file="../_cabecera.jspf" %>
<h1>Productos con stock bajo</h1>
<table>
    <tr><th>Código</th><th>Producto</th><th>Proveedor</th><th class="num">Stock</th><th class="num">Mínimo</th></tr>
    <c:forEach var="p" items="${productos}">
        <tr>
            <td><c:out value="${p.codigo}"/></td>
            <td><c:out value="${p.nombre}"/></td>
            <td><c:out value="${p.proveedor.nombre}"/></td>
            <td class="num bajo">${p.stock}</td>
            <td class="num">${p.stockMinimo}</td>
        </tr>
    </c:forEach>
    <c:if test="${empty productos}"><tr><td colspan="5">Todo el inventario está por encima del mínimo.</td></tr></c:if>
</table>
<%@ include file="../_pie.jspf" %>
