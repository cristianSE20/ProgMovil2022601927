import java.time.LocalDate
import java.time.Period
import java.time.temporal.ChronoUnit
import java.time.format.DateTimeFormatter

fun main(){
    var n1 = 0
    var n2 = 0
    var n3 = 0
    var nombre = ""
    var fechaNacimiento: LocalDate = LocalDate.now()

    println("Selecciona una opcion: ")
    val opcion = readLine()?.toIntOrNull()

    when(opcion){
        1 -> suma(n1,n2,n3)
        2 -> nombreC(nombre)
        3 -> calcularTiempoVivido(fechaNacimiento)
        4 -> salirDelPrograma()
        else -> println("Opcion no valida")
    }

}

fun suma(n1: Int, n2: Int, n3: Int){
    println("Inserte el primer numero: ")
    var n1 = readLine()!!.toInt()
    println("Inserte el segundo numero: ")
    var n2 = readLine()!!.toInt()
    println("Inserte el tercer numero: ")
    var n3 = readLine()!!.toInt()

    val res = n1 + n2 +n3

    println("El valor de la suma es: $res")

}

fun nombreC(nombre: String){
    println("Inserte su nombre completo")
    var nombre = readLine()!!.toString()

    println("Tu nombre completo es: $nombre")
}

fun calcularTiempoVivido(fechaNacimiento: LocalDate){
    var formato = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    print("Ingrese su fecha de nacimiento (dd/MM/yyyy): ")
    var entrada = readLine()!!
    var fechaNacimiento = LocalDate.parse(entrada, formato)

    var hoy = LocalDate.now()
    var diasVividos = ChronoUnit.DAYS.between(fechaNacimiento, hoy)
    var semanasVividas = diasVividos / 7
    var mesesVividos = ChronoUnit.MONTHS.between(fechaNacimiento, hoy)
    var horasVividas = diasVividos * 24
    var minutosVividos = horasVividas * 60
    var segundosVividos = minutosVividos * 60

    println("\nHas vivido:")
    println("$mesesVividos meses")
    println("$semanasVividas semanas")
    println("$diasVividos días")
    println("$horasVividas horas")
    println("$minutosVividos minutos")
    println("$segundosVividos segundos")
}

fun salirDelPrograma() {
    println("Cerrando el programa... ¡Hasta luego!")
    kotlin.system.exitProcess(0)
}