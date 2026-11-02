<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="titulo" value="Usuarios"/>
<%@ include file="../_cabecera.jspf" %>
<h1>Usuarios</h1>
<p><a class="boton" href="${ctx}/usuarios/nuevo">Nuevo usuario</a></p>
<table>
    <tr><th>Usuario</th><th>Nombre</th><th>Correo</th><th>Rol</th><th>Estado</th><th></th></tr>
    <c:forEach var="u" items="${usuarios}">
        <tr>
            <td><c:out value="${u.username}"/></td>
            <td><c:out value="${u.nombre}"/></td>
            <td><c:out value="${u.email}"/></td>
            <td>${u.rol}</td>
            <td>${u.activo ? 'Activo' : 'Desactivado'}</td>
            <td>
                <a href="${ctx}/usuarios/${u.id}/editar">Editar</a>
                <form class="en-linea" method="post" action="${ctx}/usuarios/${u.id}/estado">
                    <button class="${u.activo ? 'rojo' : 'gris'}" type="submit">${u.activo ? 'Desactivar' : 'Activar'}</button>
                </form>
            </td>
        </tr>
    </c:forEach>
</table>
<%@ include file="../_pie.jspf" %>
