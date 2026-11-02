<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="titulo" value="Error"/>
<%@ include file="_cabecera.jspf" %>
<div class="caja">
    <h1>${codigo} - <c:out value="${tituloError}"/></h1>
    <p><c:out value="${mensaje}"/></p>
    <a class="boton gris" href="${ctx}/dashboard">Volver al inicio</a>
</div>
<%@ include file="_pie.jspf" %>
