# Docker swarm cluster with manager node in ict9

Docker swarm manager ict9 token:

```
/* node joining */
docker swarm join --token SWMTKN-1-567i93wtptd8rxwj4m28fwkzuzqas7x87slcdo3ignywf8ywzd-8vqbf1nqmwpeadypfpznaa0f9 192.168.22.9:2377
```

Since docker swarm does not properly propagate network for worker node, after
creating overlay network, run a simple container on proper worker host for
network testing.

```
/* worker host */
docker run -it --rm --name alpine2 --network <SWARM_NOETWORK> alpine
```

Good side for hadoop on docker swarm

```
https://blog.newnius.com/setup-distributed-hadoop-cluster-with-docker-step-by-step.html
```

Thinking setup by ourself a new single docker data node just for now (2022-06-02).
