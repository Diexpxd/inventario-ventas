<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="titulo" value="Movimientos"/>
<%@ include file="../_cabecera.jspf" %>
<h1>Movimientos de inventario</h1>
<form class="caja fila" method="post" action="${ctx}/movimientos">
    <div><label for="idProducto">Producto</label>
        <select id="idProducto" name="idProducto" required>
            <c:forEach var="p" items="${productos}">
                <option value="${p.id}"><c:out value="${p.codigo}"/> - <c:out value="${p.nombre}"/> (hay ${p.stock})</option>
            </c:forEach>
        </select></div>
    <div><label for="tipo">Tipo</label>
        <select id="tipo" name="tipo">
            <c:forEach var="t" items="${tipos}"><option value="${t}">${t}</option></c:forEach>
        </select></div>
    <div><label for="cantidad">Cantidad</label><input id="cantidad" name="cantidad" type="number" value="1" required></div>
    <div><label for="referencia">Referencia</label><input id="referencia" name="referencia" maxlength="50"></div>
    <div style="align-self:flex-end"><button type="submit">Registrar</button></div>
</form>
<table>
    <tr><th>Fecha</th><th>Producto</th><th>Tipo</th><th class="num">Cantidad</th><th>Usuario</th><th>Referencia</th></tr>
    <c:forEach var="m" items="${movimientos}">
        <tr>
            <td>${fn:replace(fn:substring(m.fecha, 0, 16), 'T', ' ')}</td>
            <td><c:out value="${m.producto.nombre}"/></td>
            <td>${m.tipo}</td>
            <td class="num">${m.cantidad}</td>
            <td><c:out value="${m.usuario.nombre}"/></td>
            <td><c:out value="${m.referencia}"/></td>
        </tr>
    </c:forEach>
    <c:if test="${empty movimientos}"><tr><td colspan="6">Sin movimientos.</td></tr></c:if>
</table>
<%@ include file="../_pie.jspf" %>
