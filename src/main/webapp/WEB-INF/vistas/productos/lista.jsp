<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="titulo" value="Productos"/>
<%@ include file="../_cabecera.jspf" %>
<h1>Catálogo de productos</h1>
<form class="fila" method="get" action="${ctx}/productos" style="align-items:center;margin-bottom:14px">
    <div style="flex:3"><input name="q" value="<c:out value='${q}'/>" placeholder="Buscar por nombre o código"></div>
    <div style="flex:1">
        <select name="categoria">
            <option value="">Categoría</option>
            <c:forEach var="cat" items="${categorias}">
                <option value="${cat.id}" ${cat.id == categoriaSel ? 'selected' : ''}><c:out value="${cat.nombre}"/></option>
            </c:forEach>
        </select></div>
    <div style="flex:0"><button class="gris" type="submit">Buscar</button></div>
    <c:if test="${puedeEditar}"><div style="flex:0"><a class="boton verde" href="${ctx}/productos/nuevo" style="white-space:nowrap">+ Nuevo</a></div></c:if>
</form>
<table>
    <tr><th>Código</th><th>Nombre</th><th>Categoría</th><th class="num">Precio</th><th class="num">Stock</th><c:if test="${puedeEditar}"><th>Acciones</th></c:if></tr>
    <c:forEach var="p" items="${productos}">
        <tr class="${p.stock <= p.stockMinimo ? 'bajo-min' : ''}">
            <td><c:out value="${p.codigo}"/></td>
            <td><c:out value="${p.nombre}"/></td>
            <td><c:out value="${p.categoria.nombre}"/></td>
            <td class="num">$ <fmt:formatNumber value="${p.precio}" pattern="#,##0.00"/></td>
            <td class="num">${p.stock}</td>
            <c:if test="${puedeEditar}">
            <td>
                <a href="${ctx}/productos/${p.id}/editar">Editar</a>
                <form class="en-linea" method="post" action="${ctx}/productos/${p.id}/desactivar"
                      onsubmit="return confirm('¿Desactivar este producto?');">
                    <button class="enlace" type="submit">Desactivar</button>
                </form>
            </td>
            </c:if>
        </tr>
    </c:forEach>
    <c:if test="${empty productos}"><tr><td colspan="6">No hay productos que coincidan.</td></tr></c:if>
</table>
<p class="nota">Las filas amarillas tienen el stock por debajo del mínimo.</p>
<%@ include file="../_pie.jspf" %>
