<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="titulo" value="Venta"/>
<%@ include file="../_cabecera.jspf" %>
<h1>Venta ${venta.folio} <span class="etiqueta ${venta.estado}">${venta.estado}</span></h1>
<div class="caja">
    <p>Fecha: ${fn:replace(fn:substring(venta.fecha, 0, 16), 'T', ' ')}<br>
       Cliente: <c:out value="${venta.cliente}"/><br>
       Vendedor: <c:out value="${venta.vendedor.nombre}"/></p>
</div>
<table>
    <tr><th>Producto</th><th class="num">Precio</th><th class="num">Cantidad</th><th class="num">Subtotal</th></tr>
    <c:forEach var="d" items="${venta.detalles}">
        <tr>
            <td><c:out value="${d.producto.nombre}"/></td>
            <td class="num">$<fmt:formatNumber value="${d.precioUnitario}" pattern="#,##0.00"/></td>
            <td class="num">${d.cantidad}</td>
            <td class="num">$<fmt:formatNumber value="${d.subtotal}" pattern="#,##0.00"/></td>
        </tr>
    </c:forEach>
    <tr><th colspan="3" class="num">Total</th><th class="num">$<fmt:formatNumber value="${venta.total}" pattern="#,##0.00"/></th></tr>
</table>
<p>
    <a class="boton gris" href="${ctx}/ventas">Volver al historial</a>
    <c:if test="${venta.estado != 'CANCELADA'}">
        <form class="en-linea" method="post" action="${ctx}/ventas/${venta.id}/cancelar"
              onsubmit="return confirm('¿Cancelar la venta y regresar el stock?');">
            <button class="rojo" type="submit">Cancelar venta</button>
        </form>
    </c:if>
</p>
<%@ include file="../_pie.jspf" %>
