# NOVA Market

Aplicación web del laboratorio de **Arquitectura de Software — Semana 11** de la Universidad Privada San Juan Bautista. Implementa los patrones de comportamiento **Strategy** y **Observer** en un flujo funcional de registro y confirmación de pedidos.

## Funcionalidades

- Registro de pedidos asociados a cliente, categoría e importe.
- Descuentos Strategy: nuevo 5 %, frecuente 10 % y no identificado 0 %.
- Cálculos con `BigDecimal`, dos decimales y `RoundingMode.HALF_UP`.
- Estados de pedido `PENDING` y `CONFIRMED`.
- Confirmación idempotente y transición segura en memoria.
- Evento único `OrderConfirmedEvent` por pedido confirmado.
- Observadores de cliente, almacén y administración.
- Aislamiento de fallos: un observador no interrumpe a los demás.
- Historial visible de eventos y resultados por observador.
- Dashboard, listado, formulario, detalle de pedido e historial responsive.
- JAR ejecutable y Docker multi-stage preparado para un despliegue posterior.

No se envían correos ni mensajes externos. Las notificaciones son acciones internas reales, almacenadas en memoria y mostradas como evidencia del patrón Observer.

## Tecnologías

- Java 17
- Spring Boot 3.5.16
- Spring Web MVC, Validation y Thymeleaf
- Maven Wrapper
- HTML5, CSS3 y JavaScript sin frontend separado
- JUnit 5 y Spring Boot Test
- Docker multi-stage

## Arquitectura

```text
src/main/java/pe/edu/upsjb/novamarket/
├── domain/
│   ├── discount/        # Strategy
│   ├── event/           # Contratos y modelos de Observer
│   ├── model/           # Cliente, pedido, estados y transiciones
│   └── repository/      # Puertos de persistencia
├── application/         # Casos de uso, publisher e historial
├── infrastructure/
│   ├── config/          # Registro de dependencias
│   ├── observer/        # Tres observadores concretos
│   └── persistence/     # Repositorios concurrentes en memoria
└── presentation/
    ├── dashboard/
    ├── event/
    ├── error/
    └── order/
```

### Flujo Observer

```text
OrderController
    │
    ▼
OrderConfirmationService
    │  transición atómica PENDING → CONFIRMED
    ▼
OrderRepository
    │
    ├── si ya estaba confirmado: termina sin publicar
    │
    └── si es una transición nueva
            │
            ▼
      OrderConfirmedEvent
            │
            ▼
      OrderEventPublisher
            ├── CustomerNotificationObserver
            ├── WarehouseObserver
            └── AdministrationObserver
                    │
                    ▼
          EventHistoryRepository
```

El publisher recibe `List<OrderEventObserver>` mediante inyección. Un nuevo observador se incorpora implementando la interfaz y registrándolo como bean; no es necesario modificar `OrderService`, `OrderConfirmationService` ni el publisher.

### Idempotencia y concurrencia

`InMemoryOrderRepository.confirmPending` utiliza `ConcurrentHashMap.computeIfPresent`. Solo la primera solicitud obtiene una transición nueva. Las siguientes reciben el pedido ya confirmado y no generan otro UUID ni procesamientos adicionales.

## Ejecución en Windows

Desde PowerShell, en la raíz del proyecto:

```powershell
java -version
.\mvnw.cmd clean test
.\mvnw.cmd spring-boot:run
```

Abrir `http://localhost:8080`.

Para generar y ejecutar el JAR:

```powershell
.\mvnw.cmd clean package
java -jar .\target\novamarket.jar
```

Para usar otro puerto:

```powershell
$env:PORT = "9090"
java -jar .\target\novamarket.jar
```

## Rutas web

| Método | Ruta | Función |
|---|---|---|
| GET | `/` | Dashboard e indicadores |
| GET | `/orders` | Listado de pedidos y estados |
| GET | `/orders/new` | Formulario de registro |
| POST | `/orders` | Valida y registra un pedido pendiente |
| GET | `/orders/{id}` | Detalle, descuento y trazabilidad del pedido |
| POST | `/orders/{id}/confirm` | Confirma idempotentemente el pedido |
| GET | `/events` | Historial de eventos y acciones Observer |

## Pruebas

```powershell
.\mvnw.cmd clean test
```

La suite comprueba:

- los tres descuentos y el redondeo monetario;
- registro y agregación de pedidos;
- cambio de `PENDING` a `CONFIRMED`;
- generación de un evento con UUID;
- recepción del mismo evento por los tres observadores;
- historial con las acciones esperadas;
- confirmación repetida sin duplicados;
- confirmaciones concurrentes sin eventos duplicados;
- incorporación de un observador adicional;
- aislamiento y trazabilidad de fallos de observadores;
- carga completa del contexto Spring Boot.

## Demostración académica

1. Abrir `/orders/new`.
2. Registrar un pedido de S/ 200.00 como cliente frecuente.
3. Mostrar el descuento Strategy de S/ 20.00 y total de S/ 180.00.
4. Abrir el detalle desde `/orders` y señalar el estado **Pendiente**.
5. Presionar **Confirmar pedido**.
6. Mostrar el estado **Confirmado**, el UUID del evento y las tres acciones.
7. Abrir `/events` y comprobar que los tres resultados comparten el mismo UUID.
8. Repetir `POST /orders/{id}/confirm` si se desea demostrar idempotencia; la cantidad de eventos y acciones no cambia.

## Docker y Render

```powershell
docker build -t novamarket:stage2 .
docker run --rm -p 8080:8080 novamarket:stage2
```

El proyecto conserva:

- `Dockerfile` multi-stage con Java 17;
- `.dockerignore` y `.gitignore`;
- `server.address=0.0.0.0`;
- `server.port=${PORT:8080}`;
- JAR `target/novamarket.jar`;
- ausencia de secretos y rutas locales en la aplicación.

No se ha realizado ningún despliegue.

## Limitaciones actuales

- Pedidos, eventos y procesamientos se pierden al reiniciar la aplicación.
- Los observadores ejecutan acciones internas; no existen correos ni integraciones externas.
- No hay autenticación, pagos ni base de datos porque están fuera del alcance del laboratorio.
- El historial no implementa paginación; está orientado al volumen pequeño de la demostración académica.
