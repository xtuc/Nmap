## [Nmap](https://nmap.org) clone written in Scala

The application uses Actors to dispatch pings throught multiple threads. This project has an educatif goals.

### Added features
- Support IP subnet
- SLOC In Scala

### Usage

**nmap.jar [host(s)] [port|port start] ([port end])**

Check if the port 80 is open on your local host.
```shell
nmap.jar localhost 80
```

Check if ports 21 to 80 are open on your local host
```shell
nmap.jar localhost 21 80
```

Check if port 22 is open on your entier network
```shell
nmap.jar 192.168.1.0/24 22
```

### Known issues
- Hostname resolve doesn't work with subnet
- No thread spawn limitation with ping sender Actor
- Can't use remote Actors since a lock is used