# Porting ulake project to kurbernetes cluster

## Generate k8s relying on docker-engine (cri-docker) and default flannel network

Since k8s is no longer includes docker battery, it is mandatory to configure
docker shim and setup kubernetes using this CRI. Noting that there are multiple
CRI implementation different than cri-docker and docker-engine but this CRI forcing
kubernetes setup containers directly on docker namespace and hence allowing admin to
monitor k8s process through docker-cli (common working tool for current developers).
Therefore, it is suggestion to follow this set of combination on whole k8s cluster.

Booting up individual kubernetes node is just a half way, for generating a k8s
cluster, each node and pod need to be configured in single internal network for
communicating and working together. Then the project uses flannel solution to
handle this issues which is a simple CNI pluggin providing platform to forward
package between different k8s node. The project rely on it as  the core
component to build up k8s cluster internal network.

## Detail setup

1. cri-docker install:

```
### Debian based systems ###
sudo apt update
sudo apt install git wget curl

VER=$(curl -s https://api.github.com/repos/Mirantis/cri-dockerd/releases/latest|grep tag_name | cut -d '"' -f 4|sed 's/v//g')
echo $VER

### For Intel 64-bit CPU ###
wget https://github.com/Mirantis/cri-dockerd/releases/download/v${VER}/cri-dockerd-${VER}.amd64.tgz
tar xvf cri-dockerd-${VER}.amd64.tgz

### For ARM 64-bit CPU ###
wget https://github.com/Mirantis/cri-dockerd/releases/download/v${VER}/cri-dockerd-${VER}.arm64.tgz
cri-dockerd-${VER}.arm64.tgz

sudo mv cri-dockerd/cri-dockerd /usr/local/bin/

wget https://raw.githubusercontent.com/Mirantis/cri-dockerd/master/packaging/systemd/cri-docker.service
wget https://raw.githubusercontent.com/Mirantis/cri-dockerd/master/packaging/systemd/cri-docker.socket
sudo mv cri-docker.socket cri-docker.service /etc/systemd/system/
sudo sed -i -e 's,/usr/bin/cri-dockerd,/usr/local/bin/cri-dockerd,' /etc/systemd/system/cri-docker.service

sudo systemctl daemon-reload
sudo systemctl enable cri-docker.service
sudo systemctl enable --now cri-docker.socket
```

**RECHECK**

```
sudo ls /var/run/cri-dockerd.sock
==> existed
```

2. k8s control panel boot:

```
sudo kubeadm init --pod-network-cidr 10.244.0.0/16 --cri-socket unix:///var/run/cri-dockerd.sock
sudo cp /etc/kubernetes/admin.conf ~/.kube/config
sudo chown $(id -u):$(id -g) ~/.kube/config
```

**RECHECK**

```
sudo netstat -tlnp
==> expect kube-apiserver kubelet kube-proxy etcd kube-control kube-schedul

sudo kubectl get node
==> expect node information (network not ready)
```

3. flannel apply:

```
kubectl apply -f https://raw.githubusercontent.com/flannel-io/flannel/master/Documentation/kube-flannel.yml
```

**RECHECK**

```
sudo kubectl get node
==> expect node information (netowrk ready)
```

4. k8s worker join:

```
### GET JOIN TOKEN FROM MANAGER NODE
scrip=$(kubeadm token create --print-join-command)

### JOIN FROM WORKER NODE
scrip --cri-socket unix:///var/run/cri-dockerd.sock
```
