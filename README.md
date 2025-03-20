# Apache Druid H3 Extension

This extension allows you to use Uber's H3 geospatial indexing system functions within Apache Druid.

## Overview

[H3](https://h3geo.org/) is a hexagon-based geospatial indexing system that provides an efficient way to index, query,
and analyze location data hierarchically. This Druid extension makes H3's core functions available directly within Druid
SQL queries.

## Features

This extension supports the following H3 functions:

- Converting latitude/longitude coordinates to H3 indexes
- Converting H3 indexes to latitude/longitude coordinates
- Calculating H3 neighborhood rings
- Computing distances between H3 indexes
- Validating H3 indexes
- Retrieving H3 cell boundaries in GeoJSON format

## Requirements

- Apache Druid 0.23.0 or newer
- Java 8 or newer
- Maven 3.x

## Installation

1. Build the extension:
   ```bash
   mvn clean package
   ```

2. Place the generated JAR file and all dependencies into the Druid extensions folder:
   ```bash
   mkdir -p ${DRUID_HOME}/extensions/h3-extension/
   cp target/h3-druid-extension-*.jar ${DRUID_HOME}/extensions/h3-extension/
   cp target/dependency/h3-*.jar ${DRUID_HOME}/extensions/h3-extension/
   ```

3. Update the Druid configuration file (`conf/druid/cluster/_common/common.runtime.properties`):
   ```properties
   druid.extensions.loadList=["h3-extension", ... other extensions ...]
   ```

4. Restart Druid.

## Usage

### Functions

#### `h3_geo_to_h3(lat, lon, resolution)`

Converts latitude and longitude coordinates to an H3 index.

```sql
SELECT h3_geo_to_h3(40.689247, -74.044502, 7) AS h3_index
```

#### `h3_h3_to_geo(h3Index)`

Converts an H3 index to the coordinates of its center point.

```sql
SELECT h3_h3_to_geo('872830828ffffff') AS coordinates
```

#### `h3_k_ring(h3Index, k)`

Returns a list of cells within distance k of the given H3 index.

```sql
SELECT h3_k_ring('872830828ffffff', 1) AS neighbors
```

#### `h3_distance(h3Index1, h3Index2)`

Calculates the distance between two H3 indexes.

```sql
SELECT h3_distance('872830828ffffff', '872830829ffffff') AS distance
```

#### `h3_is_valid(h3Index)`

Checks if an H3 index is valid.

```sql
SELECT h3_is_valid('872830828ffffff') AS is_valid
```

#### `h3_h3_to_boundary(h3Index)`

Returns the boundary geometry of an H3 cell in GeoJSON format.

```sql
SELECT h3_h3_to_boundary('872830828ffffff') AS boundary
```

### Example SQL Queries

Indexing locations in an events table at a specific H3 resolution:

```sql
SELECT event_id,
       event_time,
       h3_geo_to_h3(latitude, longitude, 7) AS h3_cell
FROM events
```

Event count aggregated by H3 cells:

```sql
SELECT h3_geo_to_h3(latitude, longitude, 7) AS h3_cell,
       COUNT(*)                             AS event_count
FROM events
GROUP BY 1
ORDER BY 2 DESC
```

Finding events within 3 rings of a specific point:

```sql
WITH center AS (SELECT h3_geo_to_h3(40.7128, -74.0060, 7) AS center_h3)
SELECT e.event_id,
       e.event_time,
       e.latitude,
       e.longitude,
       h3_geo_to_h3(e.latitude, e.longitude, 7) AS event_h3,
       h3_distance(
               h3_geo_to_h3(e.latitude, e.longitude, 7),
               center.center_h3
       )                                        AS distance
FROM events e
         CROSS JOIN center
WHERE h3_distance(
              h3_geo_to_h3(e.latitude, e.longitude, 7),
              center.center_h3
      ) <= 3
```

## Development

### Project Structure

```
src/main/java/org/example/druid/h3/
├── H3Functions.java         # Implementations of all H3 functions
├── H3Module.java            # Druid module definition
├── DruidH3Bindings.java     # Binding functions to Druid
└── H3FunctionExprMacro.java # ExprMacro implementation

src/main/resources/META-INF/services/
└── org.apache.druid.initialization.DruidModule # Service definition
```

### Testing

Run unit tests:

```bash
mvn test
```

### Contributing

1. Fork this repository
2. Create a new branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push your branch (`git push origin feature/amazing-feature`)
5. Create a Pull Request

## License

This project is licensed under the MIT License - see the `LICENSE` file for details.

## Contact

Project Owner: [Your Name](mailto:email@example.com)

Project Link: [https://github.com/username/druid-h3-extension](https://github.com/username/druid-h3-extension)

## Acknowledgments

- [Apache Druid](https://druid.apache.org/) community
- [Uber H3](https://h3geo.org/) development team