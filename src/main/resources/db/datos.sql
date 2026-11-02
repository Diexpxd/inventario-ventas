INSERT INTO categorias (id, nombre, descripcion) VALUES (1, 'Papeleria', 'Cuadernos, plumas y articulos de oficina');
INSERT INTO categorias (id, nombre, descripcion) VALUES (2, 'Electronica', 'Accesorios y cables');
INSERT INTO categorias (id, nombre, descripcion) VALUES (3, 'Hogar', 'Articulos para la casa');

INSERT INTO proveedores (id, nombre, contacto, telefono, email) VALUES (1, 'Proveedor Uno de Ejemplo', 'Contacto Uno', '5550000001', 'uno@ejemplo.com');
INSERT INTO proveedores (id, nombre, contacto, telefono, email) VALUES (2, 'Proveedor Dos de Ejemplo', 'Contacto Dos', '5550000002', 'dos@ejemplo.com');

INSERT INTO productos (id, codigo, nombre, descripcion, precio, stock, stock_minimo, id_categoria, id_proveedor) VALUES (1, 'P-001', 'Cuaderno profesional', 'Cuaderno de 100 hojas', 45.00, 40, 10, 1, 1);
INSERT INTO productos (id, codigo, nombre, descripcion, precio, stock, stock_minimo, id_categoria, id_proveedor) VALUES (2, 'P-002', 'Pluma azul', 'Pluma de tinta azul', 8.50, 120, 30, 1, 1);
INSERT INTO productos (id, codigo, nombre, descripcion, precio, stock, stock_minimo, id_categoria, id_proveedor) VALUES (3, 'P-003', 'Cable USB-C', 'Cable de 1 metro', 89.90, 4, 8, 2, 2);
INSERT INTO productos (id, codigo, nombre, descripcion, precio, stock, stock_minimo, id_categoria, id_proveedor) VALUES (4, 'P-004', 'Memoria USB 32 GB', NULL, 129.00, 15, 5, 2, 2);
INSERT INTO productos (id, codigo, nombre, descripcion, precio, stock, stock_minimo, id_categoria, id_proveedor) VALUES (5, 'P-005', 'Vaso termico', 'Vaso de acero de 500 ml', 159.00, 3, 5, 3, NULL);
