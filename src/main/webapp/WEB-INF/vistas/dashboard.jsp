<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="titulo" value="Dashboard"/>
<%@ include file="_cabecera.jspf" %>
<h1>Dashboard</h1>
<div class="tarjetas">
    <div class="tarjeta"><div>Ventas de hoy</div><div class="valor">$ <fmt:formatNumber value="${ventasHoy}" pattern="#,##0.00"/></div></div>
    <div class="tarjeta verde"><div>Productos activos</div><div class="valor">${totalProductos}</div></div>
    <div class="tarjeta roja"><div>Alertas de stock</div><div class="valor">${fn:length(bajoMinimo)}</div></div>
</div>

<c:if test="${puedeVender}">
<h2>Últimas ventas</h2>
<table>
    <tr><th>Folio</th><th>Cliente</th><th class="num">Total</th><th>Estado</th></tr>
    <c:forEach var="v" items="${ultimasVentas}">
        <tr><td><a href="${ctx}/ventas/${v.id}">${v.folio}</a></td>
            <td><c:out value="${empty v.cliente ? '-' : v.cliente}"/></td>
            <td class="num">$ <fmt:formatNumber value="${v.total}" pattern="#,##0.00"/></td>
            <td>${v.estado}</td></tr>
    </c:forEach>
    <c:if test="${empty ultimasVentas}"><tr><td colspan="4">Todavía no hay ventas.</td></tr></c:if>
</table>
</c:if>

<h2>Productos con stock bajo</h2>
<c:forEach var="p" items="${bajoMinimo}">
    <div class="alerta">! <c:out value="${p.nombre}"/> (stock: ${p.stock}, mínimo: ${p.stockMinimo})</div>
</c:forEach>
<c:if test="${empty bajoMinimo}"><p>Todo el inventario está por encima del mínimo.</p></c:if>
<%@ include file="_pie.jspf" %>
