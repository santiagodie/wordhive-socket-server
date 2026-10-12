/**
 * Convierte los retos diarios de src/xml/Dias/DiaN.xml en demo/data/retos.json,
 * que usa la demo estática para jugar sin el servidor Java.
 *
 * Uso (desde la raíz del repo, Node 18+ sin dependencias):
 *   node demo/tools/xml-a-json.mjs
 * Hay que volver a ejecutarlo y hacer commit del JSON si cambian los XML.
 *
 * Salida: { "1": { "letraCentral": "l", "letras": "iluster",
 *                  "palabras": [ { "contenido": "ele", "longitud": 3, "palabreto": false }, ... ] }, ... }
 * Se conserva el orden de las letras y de las palabras del XML, igual que hace JAXB
 * en xml.JAXB.Dia, porque el cliente y Dia.buscarPalabra dependen de ese orden.
 */
import { readdirSync, readFileSync, writeFileSync, mkdirSync } from 'node:fs';
import { dirname, join } from 'node:path';

const DIR_XML = join('src', 'xml', 'Dias');
const SALIDA = join('demo', 'data', 'retos.json');

function decodificar(texto) {
	return texto
		.replace(/&#x([0-9a-f]+);/gi, (_, h) => String.fromCodePoint(parseInt(h, 16)))
		.replace(/&#(\d+);/g, (_, d) => String.fromCodePoint(parseInt(d, 10)))
		.replace(/&lt;/g, '<').replace(/&gt;/g, '>')
		.replace(/&quot;/g, '"').replace(/&apos;/g, "'")
		.replace(/&amp;/g, '&');
}

function texto(xml, etiqueta, fichero) {
	const m = xml.match(new RegExp(`<${etiqueta}>([\\s\\S]*?)</${etiqueta}>`));
	if (!m) throw new Error(`${fichero}: falta <${etiqueta}>`);
	return decodificar(m[1]);
}

function convertirDia(xml, fichero) {
	xml = xml.replace(/<!--[\s\S]*?-->/g, '');
	const id = xml.match(/<dia\s+id="(\d+)"/);
	if (!id) throw new Error(`${fichero}: falta <dia id="...">`);

	const letras = [...xml.matchAll(/<letra>([\s\S]*?)<\/letra>/g)].map(m => decodificar(m[1]));
	const palabras = [...xml.matchAll(/<palabra\s+long="(\d+)"\s*>([\s\S]*?)<\/palabra>/g)].map(m => ({
		contenido: texto(m[2], 'contenido', fichero),
		longitud: Number(m[1]),
		palabreto: /<palabreto\s*\/>|<palabreto\s*>\s*<\/palabreto>/.test(m[2])
	}));

	const etiquetasPalabra = (xml.match(/<palabra[\s>]/g) || []).length;
	if (palabras.length !== etiquetasPalabra) {
		throw new Error(`${fichero}: ${etiquetasPalabra} <palabra> pero solo ${palabras.length} reconocidas`);
	}
	if (letras.length === 0) throw new Error(`${fichero}: no hay <letra>`);

	return {
		id: Number(id[1]),
		dia: { letraCentral: texto(xml, 'letraCentral', fichero), letras: letras.join(''), palabras }
	};
}

const retos = {};
const ficheros = readdirSync(DIR_XML)
	.filter(f => /^Dia\d+\.xml$/.test(f))
	.sort((a, b) => parseInt(a.slice(3), 10) - parseInt(b.slice(3), 10));

for (const f of ficheros) {
	const { id, dia } = convertirDia(readFileSync(join(DIR_XML, f), 'utf8'), f);
	if (retos[id]) throw new Error(`${f}: id ${id} repetido`);
	retos[id] = dia;
}

mkdirSync(dirname(SALIDA), { recursive: true });
writeFileSync(SALIDA, JSON.stringify(retos) + '\n');
const total = Object.values(retos).reduce((n, d) => n + d.palabras.length, 0);
console.log(`${ficheros.length} días y ${total} palabras -> ${SALIDA}`);
