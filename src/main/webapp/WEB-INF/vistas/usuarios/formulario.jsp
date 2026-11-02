<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="titulo" value="Usuario"/>
<%@ include file="../_cabecera.jspf" %>
<c:set var="nuevo" value="${empty u.id}"/>
<h1>${nuevo ? 'Nuevo usuario' : 'Editar usuario'}</h1>
<form class="caja" method="post" action="${ctx}/usuarios${nuevo ? '' : '/'}${u.id}">
    <div class="fila">
        <div><label for="username">Usuario</label>
            <input id="username" name="username" maxlength="50" required value="<c:out value='${u.username}'/>" ${nuevo ? '' : 'readonly'}></div>
        <div><label for="password">Contraseña ${nuevo ? '' : '(déjala vacía para no cambiarla)'}</label>
            <input id="password" name="password" type="password" ${nuevo ? 'required' : ''}></div>
    </div>
    <div class="fila">
        <div><label for="nombre">Nombre</label>
            <input id="nombre" name="nombre" maxlength="100" required value="<c:out value='${u.nombre}'/>"></div>
        <div><label for="email">Correo</label>
            <input id="email" name="email" type="email" maxlength="100" value="<c:out value='${u.email}'/>"></div>
    </div>
    <label for="rol">Rol</label>
    <select id="rol" name="rol">
        <c:forEach var="r" items="${roles}"><option value="${r}" ${r == u.rol ? 'selected' : ''}>${r}</option></c:forEach>
    </select>
    <p><button type="submit">Guardar</button> <a class="boton gris" href="${ctx}/usuarios">Cancelar</a></p>
</form>
<%@ include file="../_pie.jspf" %>
