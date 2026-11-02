<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="titulo" value="Nueva venta"/>
<%@ include file="../_cabecera.jspf" %>
<h1>Nueva venta</h1>
<form method="post" action="${ctx}/ventas">
    <div class="fila" style="align-items:flex-end">
        <div><label for="cliente">Cliente (opcional)</label><input id="cliente" name="cliente" maxlength="100"></div>
        <div><label for="codigo">Código del producto</label>
            <input id="codigo" list="lista-productos" autocomplete="off" placeholder="P-001">
            <datalist id="lista-productos">
                <c:forEach var="p" items="${productos}">
                    <c:if test="${p.stock > 0}"><option value="<c:out value='${p.codigo}'/>" data-id="${p.id}" data-precio="${p.precio}" data-stock="${p.stock}"><c:out value="${p.nombre}"/></option></c:if>
                </c:forEach>
            </datalist></div>
        <div style="flex:0"><button type="button" id="agregar">Agregar</button></div>
    </div>

    <table id="carrito" style="margin-top:14px">
        <thead><tr><th>Producto</th><th class="num">Cant.</th><th class="num">Precio</th><th class="num">Subtotal</th><th></th></tr></thead>
        <tbody></tbody>
    </table>
    <div style="margin-top:14px">
        <div class="total-caja"><div>Total</div><div class="valor" id="total">$ 0.00</div></div>
        <button type="submit" id="confirmar" class="verde" disabled>Confirmar venta</button>
        <a class="boton rojo" href="${ctx}/ventas" style="background:#dc3545">Cancelar</a>
    </div>
</form>

<script>
var PRODUCTOS = {};
document.querySelectorAll('#lista-productos option').forEach(function (o) {
    PRODUCTOS[o.value] = {id: o.dataset.id, nombre: o.textContent, precio: parseFloat(o.dataset.precio), stock: parseInt(o.dataset.stock, 10)};
});
(function () {
    var cuerpo = document.querySelector('#carrito tbody');
    var campo = document.getElementById('codigo');
    var boton = document.getElementById('confirmar');

    function dinero(n) { return '$ ' + n.toFixed(2).replace(/\B(?=(\d{3})+(?!\d))/g, ','); }

    function recalcular() {
        var total = 0;
        cuerpo.querySelectorAll('tr').forEach(function (tr) {
            var sub = parseFloat(tr.dataset.precio) * parseInt(tr.querySelector('input[name=cantidad]').value, 10);
            tr.querySelector('.sub').textContent = dinero(sub);
            total += sub;
        });
        document.getElementById('total').textContent = dinero(total);
        boton.disabled = cuerpo.children.length === 0;
    }

    document.getElementById('agregar').addEventListener('click', function () {
        var p = PRODUCTOS[campo.value.trim()];
        if (!p) { alert('No hay un producto disponible con ese código'); return; }
        var existente = cuerpo.querySelector('tr[data-id="' + p.id + '"]');
        var actual = existente ? parseInt(existente.querySelector('input[name=cantidad]').value, 10) : 0;
        if (actual + 1 > p.stock) { alert('Solo hay ' + p.stock + ' piezas de ese producto'); return; }
        if (existente) {
            var cant = existente.querySelector('input[name=cantidad]');
            cant.value = actual + 1;
            existente.querySelector('.texto-cant').textContent = actual + 1;
        } else {
            var tr = document.createElement('tr');
            tr.dataset.id = p.id;
            tr.dataset.precio = p.precio;
            tr.innerHTML = '<td></td><td class="num"><span class="texto-cant">1</span>'
                + '<input type="hidden" name="idProducto" value="' + p.id + '">'
                + '<input type="hidden" name="cantidad" value="1"></td>'
                + '<td class="num">' + dinero(p.precio) + '</td><td class="num sub"></td>'
                + '<td><button type="button" class="enlace quitar">Quitar</button></td>';
            tr.firstChild.textContent = p.nombre;
            cuerpo.appendChild(tr);
        }
        campo.value = '';
        recalcular();
    });

    cuerpo.addEventListener('click', function (e) {
        if (e.target.classList.contains('quitar')) {
            cuerpo.removeChild(e.target.closest('tr'));
            recalcular();
        }
    });
})();
</script>
<%@ include file="../_pie.jspf" %>
