# Lung Cancel Care

Sub-project serving as the utility of ULake system.

## Requirement

1. Miniconda (4.13.0)

## Manually Setup

Environment:

```
conda create -n ulake
```

Activate environment:

```
conda activate ulake
```

For installation in first time:

```
conda install -c anaconda python=3.7
conda install -c conda-forge uwsgi=2.0.20
pip install -r requirement.txt
```

Clean:

```
conda env remove -n ulake
```
