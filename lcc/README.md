# Lung Cancel Care

Sub-project serving as the utility of ULake system. Project is built up on top
of 2 systems: internal system (flaskapp) with main role of loading and processing
lung cancer image, another is quarkus system acting as wrapper of flaskapp and
serving as main interface allowing user to interact with system including features:

- Crawl necessary files from lake storage to local serving flaskapp process requirement.
- Call flaskapp with properly arguments to process lung cancer image then retrieve result.
- Provide REST API as main interface to work with external user.

## Setup

For bootup this sub-project system. Firstly, flaskapp need to be boot up properly
( see on flaskapp directory ). Then quarkus app should be started with right configuration
to link to communicate to flaskapp ( detail implementation coming soon ).
