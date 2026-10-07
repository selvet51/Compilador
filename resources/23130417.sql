CREATE DATABASE A23130417;
USE A23130417;

CREATE TABLE simbolos(
	id_tabla INT AUTO_INCREMENT PRIMARY KEY,
	id VARCHAR(50) NOT NULL,
    tipo VARCHAR(15) NOT NULL,
    clase VARCHAR(30) NOT NULL,
    ambito INT NOT NULL,
    tamaño_arreglo VARCHAR(20),
    dimension_arreglo INT,
    numero_parametros INT,
    pertenece_funcion VARCHAR(50),
    linea INT NOT NULL
);


DELIMITER //

CREATE PROCEDURE mostrarSimbolos()
BEGIN
    SELECT * FROM simbolos ORDER BY id_tabla ASC;
END //

DELIMITER ;


DELIMITER //

CREATE PROCEDURE mostrarFinal()
BEGIN
    SELECT id, tipo, clase, ambito, tamaño_arreglo, dimension_arreglo, numero_parametros, pertenece_funcion FROM simbolos ORDER BY id_tabla ASC;
END //

DELIMITER ;




DELIMITER //

CREATE PROCEDURE insertarVariable(
    IN p_id VARCHAR(50),
    IN p_tipo VARCHAR(15),
    IN p_clase VARCHAR(30),
    IN p_ambito INT,
    IN p_linea INT
)
BEGIN
    INSERT INTO simbolos(id, tipo, clase, ambito, linea) VALUES (p_id, p_tipo, p_clase, p_ambito, p_linea);
END //

DELIMITER ;





DELIMITER //

CREATE PROCEDURE borrarTodo(
)
BEGIN
    DELETE FROM simbolos;
END //

DELIMITER ;



DELIMITER //

CREATE PROCEDURE aumentarArreglo(
    IN p_id VARCHAR(50),
    IN p_tamaño_arreglo VARCHAR(20),
    IN p_dimension_arreglo INT
)
BEGIN
    UPDATE simbolos SET tamaño_arreglo = p_tamaño_arreglo, dimension_arreglo = p_dimension_arreglo, clase = "Arreglo"
    WHERE id = p_id;
END //

DELIMITER ;




DELIMITER //

CREATE PROCEDURE insertarParametro(
    IN p_id VARCHAR(50),
    IN p_tipo VARCHAR(15),
    IN p_clase VARCHAR(30),
    IN p_ambito INT,
    IN p_pertenece_funcion VARCHAR(50),
    IN p_numero_parametros INT,
    IN p_linea INT
)
BEGIN
    INSERT INTO simbolos(id, tipo, clase, ambito, pertenece_funcion, numero_parametros, linea) VALUES 
    (p_id, p_tipo, p_clase, p_ambito, p_pertenece_funcion, p_numero_parametros, p_linea);
    UPDATE simbolos SET numero_parametros = COALESCE(numero_parametros, 0) + 1 WHERE id = p_pertenece_funcion;
END //

DELIMITER ;




DELIMITER //

CREATE PROCEDURE actualizarAmbitoFuncion(
    IN p_id VARCHAR(50),
    IN p_pertenece_funcion VARCHAR(50)
)
BEGIN
    UPDATE simbolos SET pertenece_funcion = p_pertenece_funcion WHERE id = p_id;
END //

DELIMITER ;






DELIMITER //

CREATE PROCEDURE verificarSimbolo(
    IN p_id VARCHAR(50)
)
BEGIN
    SELECT ambito FROM simbolos WHERE id = p_id;
END //

DELIMITER ;








DELIMITER //

CREATE PROCEDURE buscarPorId(
    IN p_id VARCHAR(50)
)
BEGIN
    SELECT ambito FROM simbolos WHERE id = p_id;
END //

DELIMITER ;




DELIMITER //

CREATE PROCEDURE tablaAmbitos()
BEGIN
    SELECT 
    ambito,
    SUM(CASE WHEN tipo = 'Binario' THEN 1 ELSE 0 END) AS Binario,
    SUM(CASE WHEN tipo = 'Entero' THEN 1 ELSE 0 END) AS Entero,
    SUM(CASE WHEN tipo = 'Octal' THEN 1 ELSE 0 END) AS Octal,
    SUM(CASE WHEN tipo = 'Hexadecimal' THEN 1 ELSE 0 END) AS Hexadecimal,
    SUM(CASE WHEN tipo = 'Real' THEN 1 ELSE 0 END) AS Real_,
    SUM(CASE WHEN tipo = 'Exponencial' THEN 1 ELSE 0 END) AS Exponencial,
    SUM(CASE WHEN tipo = 'Cadena' THEN 1 ELSE 0 END) AS Cadena,
    SUM(CASE WHEN tipo = 'Booleano' THEN 1 ELSE 0 END) AS Booleano
FROM simbolos
GROUP BY ambito
ORDER BY ambito;
END //

DELIMITER ;



DELIMITER //

CREATE PROCEDURE tablaTotales()
BEGIN
    SELECT
    COUNT(*) AS Total
FROM simbolos
GROUP BY ambito
ORDER BY ambito;
END //

DELIMITER ;

DELIMITER //

CREATE PROCEDURE ObtenerTotalesSimbolos()
BEGIN
    SELECT 
        0 AS Bin,
        SUM(CASE WHEN tipo = 'Entero' THEN 1 ELSE 0 END) AS Dec_,
        0 AS Oct,
        0 AS Hex,
        SUM(CASE WHEN tipo = 'Real' THEN 1 ELSE 0 END) AS Real_,
        0 AS Exp,
        SUM(CASE WHEN tipo = 'Cadena' THEN 1 ELSE 0 END) AS Cadena,
        SUM(CASE WHEN tipo = 'Booleano' THEN 1 ELSE 0 END) AS Boolean_,
        COUNT(*) AS Total
    FROM simbolos;
END //

DELIMITER ;


DELIMITER //

CREATE PROCEDURE ContarSimbolos()
BEGIN
    SELECT COUNT(*) AS Total FROM simbolos;
END //

DELIMITER ;

