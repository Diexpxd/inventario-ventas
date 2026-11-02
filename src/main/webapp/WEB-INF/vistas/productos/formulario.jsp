<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="titulo" value="Producto"/>
<%@ include file="../_cabecera.jspf" %>
<c:set var="nuevo" value="${empty producto.id}"/>
<h1>${nuevo ? 'Nuevo producto' : 'Editar producto'}</h1>
<form class="caja" method="post" action="${ctx}/productos${nuevo ? '' : '/'}${producto.id}">
    <div class="fila">
        <div><label for="codigo">Código</label>
            <input id="codigo" name="codigo" maxlength="30" required value="<c:out value='${producto.codigo}'/>"></div>
        <div><label for="nombre">Nombre</label>
            <input id="nombre" name="nombre" maxlength="100" required value="<c:out value='${producto.nombre}'/>"></div>
    </div>
    <label for="descripcion">Descripción</label>
    <textarea id="descripcion" name="descripcion" rows="3"><c:out value="${producto.descripcion}"/></textarea>
    <div class="fila">
        <div><label for="precio">Precio</label>
            <input id="precio" name="precio" type="number" step="0.01" min="0.01" required value="${producto.precio}"></div>
        <div><label for="stock">Stock ${nuevo ? 'inicial' : '(solo se cambia con movimientos)'}</label>
            <input id="stock" name="stock" type="number" min="0" value="${nuevo ? 0 : producto.stock}" ${nuevo ? '' : 'readonly'}></div>
        <div><label for="stockMinimo">Stock mínimo</label>
            <input id="stockMinimo" name="stockMinimo" type="number" min="0" value="${nuevo ? 5 : producto.stockMinimo}"></div>
    </div>
    <div class="fila">
        <div><label for="idCategoria">Categoría</label>
            <select id="idCategoria" name="idCategoria" required>
                <c:forEach var="c" items="${categorias}">
                    <option value="${c.id}" ${c.id == producto.categoria.id ? 'selected' : ''}><c:out value="${c.nombre}"/></option>
                </c:forEach>
            </select></div>
        <div><label for="idProveedor">Proveedor</label>
            <select id="idProveedor" name="idProveedor">
                <option value="">Sin proveedor</option>
                <c:forEach var="pr" items="${proveedores}">
                    <option value="${pr.id}" ${pr.id == producto.proveedor.id ? 'selected' : ''}><c:out value="${pr.nombre}"/></option>
                </c:forEach>
            </select></div>
    </div>
    <p><button type="submit">Guardar</button> <a class="boton gris" href="${ctx}/productos">Cancelar</a></p>
</form>
<%@ include file="../_pie.jspf" %>
