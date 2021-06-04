Troubleshoot
============

MQTT broker not running
-----------------------

**Error message**:

.. code-block:: none

    Exception in thread "main" at.ac.ait.lablink.core.connection.ex.LowLevelCommRuntimeException: MqttClientSync isn't connected to a broker

:Problem: Your MQTT broker is not running.
:Solution: If you have just installed the MQTT broker, restarting your machine may fix this problem.
  Otherwise, you should check if the MQTT broker is running as a service.

  On **Windows**, you should check if the broker's status is ``Running`` in the Windows Services Manager.
  To open the Windows Services Manager do the following (on Windows 10):

  #. Right-click on the Start button to open the Menu
  #. Select ``Run`` 
  #. Type ``services.msc`` and press OK


