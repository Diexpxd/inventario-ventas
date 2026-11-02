<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="titulo" value="Iniciar sesión"/>
<%@ include file="_cabecera.jspf" %>
<div class="login">
    <h1>Papelería y Novedades Cumbres</h1>
    <p class="sub">Sistema de inventario y ventas</p>
    <h2>Iniciar sesión</h2>
    <form method="post" action="${ctx}/login">
        <label for="username">Usuario</label>
        <input id="username" name="username" value="<c:out value='${username}'/>" required autofocus>
        <label for="password">Contraseña</label>
        <input id="password" name="password" type="password" required>
        <p><label class="recordar"><input type="checkbox" name="recordar" value="1"> Recordarme</label></p>
        <button type="submit">Ingresar</button>
    </form>
</div>
<%@ include file="_pie.jspf" %>
