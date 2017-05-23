package boshuai.net.ntools.ui;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import boshuai.net.ntools.R;

public class ControlComputerActivity extends Activity implements OnClickListener
{
	final String mac="60-A4-4C-31-77-85";
	final String ip = "boshuai.tpddns.cn";//"192.168.1.100";
	
	Button btnStartPC;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.control_pc_layout);
		btnStartPC = (Button)findViewById(R.id.btn_start_pc);
		btnStartPC.setOnClickListener(this);
	}
	
	
	
	

	/*
	 * 远程唤醒，如果是在局域网内使用 ipOrDomain 使用 255.255.255.255即可， 在广域网内使用要使用 计算机的广域网地址或者域名。MAC格式为XX-XX-XX-XX-XX-XX
	 * 在广域网使用时，需要在路由器中进行设置，具体设置如下：
	 * 1、通过“花生壳”等工具获取一个动态DNS，即获取一个 动态IP和域名的映射关系。
	 * 2、将计算机设置为静态IP，该IP是局域网内部的IP，一般是192.168.1.X。
	 * 3、将计算机的IP和MAC地址在路由器的“IP与MAC绑定”中设置好“静态ARP绑定设置”。因为在计算机未启动时，计算机网卡的IP地址是不能生效的，因此需要将MAC与IP绑定，以使广域网数据报到达路由器时能将数据转发指定的MAC地址所对应的IP。
	 * 4、在路由器中“转发规则”将计算机需要监听的端口与ip地址添加进去。即当广域网数据包到达时，不进行NAT转发，而直接将数据转发到指定的IP（局域网IP)中的指定端口中。
	 */
	public static void remoteStartComputer(final String ipOrDomain, final String mac, final int port)
	{
		
		
		Thread thread = new Thread(new Runnable()
		{
			
			@Override
			public void run()
			{
				byte[] destMac = getMacBytes(mac);
				if(destMac==null)
					return;
				
				try
				{
					InetAddress destHost = InetAddress.getByName(ipOrDomain);
					byte[] magicBytes = new byte[102];
					for(int i=0; i<6; i++)
					{
						magicBytes[i] = (byte)0xFF;
					}
					
					for(int i=0; i<16; i++)
					{
						for(int j=0; j<destMac.length; j++)
						{
							magicBytes[6+destMac.length * i + j] = destMac[j];
						}
					}
					
					DatagramPacket dp = null;
					dp = new DatagramPacket(magicBytes, magicBytes.length, destHost, port);
					try
					{
						DatagramSocket ds = new DatagramSocket();
						try
						{
							ds.send(dp);
						} catch (IOException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						ds.close();
					} catch (SocketException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				} catch (UnknownHostException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		});
		
		thread.start();
		
	}
	
	public static byte[] getMacBytes(String macStr)
	{
		byte[] bytes = new byte[6];
		String [] hex = macStr.split("(\\:|\\-)");
		if(hex.length!=6)
			return null;
		
		for(int i=0; i<6; i++)
		{
			bytes[i] = (byte)Integer.parseInt(hex[i], 16);
		}
		return bytes;
	}





	@Override
	public void onClick(View v)
	{
		// TODO Auto-generated method stub
		if(v==btnStartPC)
		{
			
			// TODO Auto-generated method stub
			remoteStartComputer(ip, mac, 3000);
			
			
			Toast.makeText(this, "start pc command was sended", Toast.LENGTH_LONG).show();
		}
	}

}
