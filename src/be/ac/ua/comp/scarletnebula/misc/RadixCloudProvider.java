package be.ac.ua.comp.scarletnebula.misc;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import org.dasein.cloud.AsynchronousTask;
import org.dasein.cloud.CloudException;
import org.dasein.cloud.CloudProvider;
import org.dasein.cloud.InternalException;
import org.dasein.cloud.Tag;
import org.dasein.cloud.admin.AdminServices;
import org.dasein.cloud.compute.Architecture;
import org.dasein.cloud.compute.AutoScalingSupport;
import org.dasein.cloud.compute.ComputeServices;
import org.dasein.cloud.compute.MachineImage;
import org.dasein.cloud.compute.MachineImageFormat;
import org.dasein.cloud.compute.MachineImageSupport;
import org.dasein.cloud.compute.Platform;
import org.dasein.cloud.compute.SnapshotSupport;
import org.dasein.cloud.compute.VirtualMachine;
import org.dasein.cloud.compute.VirtualMachineProduct;
import org.dasein.cloud.compute.VirtualMachineSupport;
import org.dasein.cloud.compute.VmState;
import org.dasein.cloud.compute.VmStatistics;
import org.dasein.cloud.compute.VolumeSupport;
import org.dasein.cloud.dc.DataCenterServices;
import org.dasein.cloud.identity.IdentityServices;
import org.dasein.cloud.network.NetworkServices;
import org.dasein.cloud.platform.PlatformServices;

public class RadixCloudProvider extends CloudProvider
{
	Map<UUID, VirtualMachine> createdServers = new HashMap<UUID, VirtualMachine>();

	public class RadixMachineImageSupport implements MachineImageSupport
	{

		@Override
		public void downloadImage(String arg0, OutputStream arg1)
				throws CloudException, InternalException
		{
			// TODO Auto-generated method stub

		}

		@Override
		public MachineImage getMachineImage(String arg0) throws CloudException,
				InternalException
		{
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getProviderTermForImage(Locale arg0)
		{
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean hasPublicLibrary()
		{
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public AsynchronousTask<String> imageVirtualMachine(String arg0,
				String arg1, String arg2) throws CloudException,
				InternalException
		{
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public AsynchronousTask<String> imageVirtualMachineToStorage(
				String arg0, String arg1, String arg2, String arg3)
				throws CloudException, InternalException
		{
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String installImageFromUpload(MachineImageFormat arg0,
				InputStream arg1) throws CloudException, InternalException
		{
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean isImageSharedWithPublic(String arg0)
				throws CloudException, InternalException
		{
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isSubscribed() throws CloudException, InternalException
		{
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public Iterable<MachineImage> listMachineImages()
				throws CloudException, InternalException
		{
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Iterable<MachineImage> listMachineImagesOwnedBy(String arg0)
				throws CloudException, InternalException
		{
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Iterable<String> listShares(String arg0) throws CloudException,
				InternalException
		{
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Iterable<MachineImageFormat> listSupportedFormats()
				throws CloudException, InternalException
		{
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String registerMachineImage(String arg0) throws CloudException,
				InternalException
		{
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void remove(String arg0) throws CloudException,
				InternalException
		{
			// TODO Auto-generated method stub

		}

		@Override
		public Iterable<MachineImage> searchMachineImages(String arg0,
				Platform arg1, Architecture arg2) throws CloudException,
				InternalException
		{
			MachineImage foo = new MachineImage()
			{
				private static final long serialVersionUID = 1L;

			};
			return Arrays.asList(foo);
		}

		@Override
		public void shareMachineImage(String arg0, String arg1, boolean arg2)
				throws CloudException, InternalException
		{
			// TODO Auto-generated method stub

		}

		@Override
		public boolean supportsCustomImages()
		{
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean supportsImageSharing()
		{
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean supportsImageSharingWithPublic()
		{
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public String transfer(CloudProvider arg0, String arg1)
				throws CloudException, InternalException
		{
			// TODO Auto-generated method stub
			return null;
		}

	}

	@Override
	public AdminServices getAdminServices()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCloudName()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ComputeServices getComputeServices()
	{
		return new RadixComputeServices();
	}

	@Override
	public DataCenterServices getDataCenterServices()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IdentityServices getIdentityServices()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NetworkServices getNetworkServices()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PlatformServices getPlatformServices()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getProviderName()
	{
		// TODO Auto-generated method stub
		return null;
	}

	class RadixComputeServices implements ComputeServices
	{
		@Override
		public AutoScalingSupport getAutoScalingSupport()
		{
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public MachineImageSupport getImageSupport()
		{
			return new RadixMachineImageSupport();
		}

		@Override
		public SnapshotSupport getSnapshotSupport()
		{
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public VirtualMachineSupport getVirtualMachineSupport()
		{
			return new RadixVirtualMachineSupport();
		}

		@Override
		public VolumeSupport getVolumeSupport()
		{
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean hasAutoScalingSupport()
		{
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean hasImageSupport()
		{
			return true;
		}

		@Override
		public boolean hasSnapshotSupport()
		{
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean hasVirtualMachineSupport()
		{
			return true;
		}

		@Override
		public boolean hasVolumeSupport()
		{
			// TODO Auto-generated method stub
			return false;
		}

	}

	class RadixVirtualMachineSupport implements VirtualMachineSupport
	{

		@Override
		public void boot(String arg0) throws InternalException, CloudException
		{
			// TODO Auto-generated method stub

		}

		@Override
		public VirtualMachine clone(String arg0, String arg1, String arg2,
				String arg3, boolean arg4, String... arg5)
				throws InternalException, CloudException
		{
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void disableAnalytics(String arg0) throws InternalException,
				CloudException
		{
			// TODO Auto-generated method stub

		}

		@Override
		public void enableAnalytics(String arg0) throws InternalException,
				CloudException
		{
			// TODO Auto-generated method stub

		}

		@Override
		public String getConsoleOutput(String arg0) throws InternalException,
				CloudException
		{
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public VirtualMachineProduct getProduct(String arg0)
				throws InternalException, CloudException
		{
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getProviderTermForServer(Locale arg0)
		{
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public VmStatistics getVMStatistics(String arg0, long arg1, long arg2)
				throws InternalException, CloudException
		{
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Iterable<VmStatistics> getVMStatisticsForPeriod(String arg0,
				long arg1, long arg2) throws InternalException, CloudException
		{
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public VirtualMachine getVirtualMachine(String arg0)
				throws InternalException, CloudException
		{
			VirtualMachine m = createdServers.get(UUID.fromString(arg0));

			return m;
		}

		@Override
		public boolean isSubscribed() throws CloudException, InternalException
		{
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public VirtualMachine launch(String arg0, VirtualMachineProduct arg1,
				String arg2, String arg3, String arg4, String arg5,
				String arg6, boolean arg7, boolean arg8, String... arg9)
				throws InternalException, CloudException

		{
			return null;
		}

		@Override
		public VirtualMachine launch(String arg0, VirtualMachineProduct arg1,
				String arg2, String arg3, String arg4, String arg5,
				String arg6, boolean arg7, boolean arg8, String[] arg9,
				Tag... arg10) throws InternalException, CloudException
		{
			RadixVirtualMachine vm = new RadixVirtualMachine();
			vm.setName(arg3);
			UUID uuid = UUID.randomUUID();
			vm.setProviderVirtualMachineId(uuid.toString());

			createdServers.put(uuid, vm);
			return vm;
		}

		@Override
		public Iterable<String> listFirewalls(String arg0)
				throws InternalException, CloudException
		{
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Iterable<VirtualMachineProduct> listProducts(Architecture arg0)
				throws InternalException, CloudException
		{
			VirtualMachineProduct p = new VirtualMachineProduct()
			{
				private static final long serialVersionUID = 1L;

			};

			p.setName("Standard");
			return Arrays.asList(p);
		}

		@Override
		public Iterable<VirtualMachine> listVirtualMachines()
				throws InternalException, CloudException
		{
			return createdServers.values();
		}

		@Override
		public void pause(String arg0) throws InternalException, CloudException
		{
			// TODO Auto-generated method stub

		}

		@Override
		public void reboot(String arg0) throws CloudException,
				InternalException
		{
			// TODO Auto-generated method stub

		}

		@Override
		public boolean supportsAnalytics() throws CloudException,
				InternalException
		{
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void terminate(String arg0) throws InternalException,
				CloudException
		{
			// TODO Auto-generated method stub

		}

	};

	class RadixVirtualMachine extends VirtualMachine
	{
		private static final long serialVersionUID = 1L;

		@Override
		public VmState getCurrentState()
		{
			return VmState.RUNNING;
		}

		@Override
		public VirtualMachineProduct getProduct()
		{
			final VirtualMachineProduct prod = new VirtualMachineProduct();
			prod.setName("Normal");
			return prod;
		}

		@Override
		public Architecture getArchitecture()
		{
			return Architecture.I32;
		}

		@Override
		public Platform getPlatform()
		{
			return Platform.FREE_BSD;
		}

		@Override
		public String getPublicDnsAddress()
		{
			return "radix.cmi.ua.ac.be";
		}
	}
}
